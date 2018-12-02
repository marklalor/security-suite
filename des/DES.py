"""
DES cipher
Takes in 64 bit key, message and encrypts
""" 

#initial permutation table
IP = (
    58, 50, 42, 34, 26, 18, 10, 2,
    60, 52, 44, 36, 28, 20, 12, 4,
    62, 54, 46, 38, 30, 22, 14, 6,
    64, 56, 48, 40, 32, 24, 16, 8,
    57, 49, 41, 33, 25, 17, 9,  1,
    59, 51, 43, 35, 27, 19, 11, 3,
    61, 53, 45, 37, 29, 21, 13, 5,
    63, 55, 47, 39, 31, 23, 15, 7
)

#inverse of initial permutation (final permutation) table
IP_INV = (
    40,  8, 48, 16, 56, 24, 64, 32,
    39,  7, 47, 15, 55, 23, 63, 31,
    38,  6, 46, 14, 54, 22, 62, 30,
    37,  5, 45, 13, 53, 21, 61, 29,
    36,  4, 44, 12, 52, 20, 60, 28,
    35,  3, 43, 11, 51, 19, 59, 27,
    34,  2, 42, 10, 50, 18, 58, 26,
    33,  1, 41,  9, 49, 17, 57, 25
)

# removes 8 parity bits
PC1 = (
    57, 49, 41, 33, 25, 17, 9,
    1,  58, 50, 42, 34, 26, 18,
    10, 2,  59, 51, 43, 35, 27,
    19, 11, 3,  60, 52, 44, 36,
    63, 55, 47, 39, 31, 23, 15,
    7,  62, 54, 46, 38, 30, 22,
    14, 6,  61, 53, 45, 37, 29,
    21, 13, 5,  28, 20, 12, 4
)

# last step in creating 48 bit subkey
PC2 = (
    14, 17, 11, 24, 1,  5,
    3,  28, 15, 6,  21, 10,
    23, 19, 12, 4,  26, 8,
    16, 7,  27, 20, 13, 2,
    41, 52, 31, 37, 47, 55,
    30, 40, 51, 45, 33, 48,
    44, 49, 39, 56, 34, 53,
    46, 42, 50, 36, 29, 32
)
 
 #expansion table
E  = (
    32, 1,  2,  3,  4,  5,
    4,  5,  6,  7,  8,  9,
    8,  9,  10, 11, 12, 13,
    12, 13, 14, 15, 16, 17,
    16, 17, 18, 19, 20, 21,
    20, 21, 22, 23, 24, 25,
    24, 25, 26, 27, 28, 29,
    28, 29, 30, 31, 32, 1
)
 
# permutation box
P = (
    16,  7, 20, 21,
    29, 12, 28, 17,
    1, 15, 23, 26,
    5, 18, 31, 10,
    2,  8, 24, 14,
    32, 27,  3,  9,
    19, 13, 30,  6,
    22, 11, 4,  25
)

# substitution box
Sbox = {
    0: (
        14,  4, 13,  1,  2, 15, 11,  8,  3, 10,  6, 12,  5,  9,  0,  7,
        0, 15,  7,  4, 14,  2, 13,  1, 10,  6, 12, 11,  9,  5,  3,  8,
        4,  1, 14,  8, 13,  6,  2, 11, 15, 12,  9,  7,  3, 10,  5,  0,
        15, 12,  8,  2,  4,  9,  1,  7,  5, 11,  3, 14, 10,  0,  6, 13
    ),
    1: (
        15,  1,  8, 14,  6, 11,  3,  4,  9,  7,  2, 13, 12,  0,  5, 10,
        3, 13,  4,  7, 15,  2,  8, 14, 12,  0,  1, 10,  6,  9, 11,  5,
        0, 14,  7, 11, 10,  4, 13,  1,  5,  8, 12,  6,  9,  3,  2, 15,
        13,  8, 10,  1,  3, 15,  4,  2, 11,  6,  7, 12,  0,  5, 14,  9 
    ),
    2: (
        10,  0,  9, 14,  6,  3, 15,  5,  1, 13, 12,  7, 11,  4,  2,  8,
        13,  7,  0,  9,  3,  4,  6, 10,  2,  8,  5, 14, 12, 11, 15,  1,
        13,  6,  4,  9,  8, 15,  3,  0, 11,  1,  2, 12,  5, 10, 14,  7,
        1, 10, 13,  0,  6,  9,  8,  7,  4, 15, 14,  3, 11,  5,  2, 12 
    ),
    3: (
        7, 13, 14,  3,  0,  6,  9, 10,  1,  2,  8,  5, 11, 12,  4, 15,
        13,  8, 11,  5,  6, 15,  0,  3,  4,  7,  2, 12,  1, 10, 14,  9,
        10,  6,  9,  0, 12, 11,  7, 13, 15,  1,  3, 14,  5,  2,  8,  4,
        3, 15,  0,  6, 10,  1, 13,  8,  9,  4,  5, 11, 12,  7,  2, 14
    ),
    4: (
        2, 12,  4,  1,  7, 10, 11,  6,  8,  5,  3, 15, 13,  0, 14,  9,
        14, 11,  2, 12,  4,  7, 13,  1,  5,  0, 15, 10,  3,  9,  8,  6,
        4,  2,  1, 11, 10, 13,  7,  8, 15,  9, 12,  5,  6,  3,  0, 14,
        11,  8, 12,  7,  1, 14,  2, 13,  6, 15,  0,  9, 10,  4,  5,  3
    ),
    5: (
        12,  1, 10, 15,  9,  2,  6,  8,  0, 13,  3,  4, 14,  7,  5, 11,
        10, 15,  4,  2,  7, 12,  9,  5,  6,  1, 13, 14,  0, 11,  3,  8,
        9, 14, 15,  5,  2,  8, 12,  3,  7,  0,  4, 10,  1, 13, 11,  6,
        4,  3,  2, 12,  9,  5, 15, 10, 11, 14,  1,  7,  6,  0,  8, 13
    ),
    6: (
        4, 11,  2, 14, 15,  0,  8, 13,  3, 12,  9,  7,  5, 10,  6,  1,
        13,  0, 11,  7,  4,  9,  1, 10, 14,  3,  5, 12,  2, 15,  8,  6,
        1,  4, 11, 13, 12,  3,  7, 14, 10, 15,  6,  8,  0,  5,  9,  2,
        6, 11, 13,  8,  1,  4, 10,  7,  9,  5,  0, 15, 14,  2,  3, 12
    ),
    7: (
        13,  2,  8,  4,  6, 15, 11,  1, 10,  9,  3, 14,  5,  0, 12,  7,
        1, 15, 13,  8, 10,  3,  7,  4, 12,  5,  6, 11,  0, 14,  9,  2,
        7, 11,  4,  1,  9, 12, 14,  2,  0,  6, 10, 13, 15,  3,  5,  8,
        2,  1, 14,  7,  4, 10,  8, 13, 15, 12,  9,  0,  3,  5,  6, 11
    )
}
 

# Given bytearrays key and message enciphers the message using the key
def encipher(key_bytes, message_bytes):
    diff = 8 - len(key_bytes)
    if (diff > 0):
        #pad the byte array
        byte = bytearray(8 - len(key_bytes))
        key_bytes[:0] = byte
    elif (diff < 0):
        raise ValueError("You are NOT allowed to provide a KEY greater than 8 bytes long")
    
    msg_diff = 8 - len(message_bytes)
    if (msg_diff > 0):
        #pad byte array
        byte = bytearray(8 - len(message_bytes))
        message_bytes[8:] = byte
    elif (msg_diff < 0):
        raise ValueError("You are NOT allowed to provide a MESSAGE greater than 8 bytes long")
    
    key = int.from_bytes(key_bytes, byteorder='big', signed=False)
    message = int.from_bytes(message_bytes, byteorder='big', signed=False)
    cipher_text = encryption(key, message)
    print("This is encrypted: {:x}".format(cipher_text))
    result = bin(cipher_text)
    # ****** use this to get back value for printing to GUI ==> print("{:x}".format(int.from_bytes(bytearray(int(result, 2).to_bytes((len(result) + 7) // 8, 'big')), byteorder='big', signed=False)))
    return bytearray(int(result, 2).to_bytes((len(result) + 7) // 8, 'big')) #To display correctly , you need to format with {:x}

# Given bytearrays key and ciphertext deciphers the message using the key
def decipher(key_bytes, ciphered_text_bytes):
    diff = 8 - len(key_bytes)
    if (diff > 0):
        #pad the byte array
        byte = bytearray(8 - len(key_bytes))
        key_bytes[:0] = byte
    elif (diff < 0):
        #remove from right until 8 bytes 
        key_bytes = key_bytes[:8]
        
    key = int.from_bytes(key_bytes, byteorder='big', signed=False)
    ciphered_text = int.from_bytes(ciphered_text_bytes, byteorder='big', signed=False)
    plain = encryption(key, ciphered_text, decryption=True)
    print("This is decrypted {:x}".format(plain))
    result = bin(plain)
    #**** use this to get a printable value for GUI from bytearray ==> print("{:x}".format(int.from_bytes(bytearray(int(result, 2).to_bytes((len(result) + 7) // 8, 'big')), byteorder='big', signed=False)))
    return bytearray(int(result, 2).to_bytes((len(result) + 7) // 8, 'big'))

# Takes 64 bit key and message , encrypts message with key
def encryption(key, message, decryption=False):
    assert isinstance(key, int) and isinstance(message, int)
    assert key.bit_length() <= 64
    assert  message.bit_length() <= 64

    key = permute_using_table(PC1, key, 64) #yields 64 -> 56 bits

    #split key into 2 pieces
    C0 = key >> 28
    D0 = key & (2 ** 28 - 1)

    #create the 16 round keys
    r_keys = make_round_keys(C0, D0) #yields 56 -> 48 bit

    block_message = permute_using_table(IP, message, 64) 
    
    #split plaintext block into 2 pieces
    L0 = block_message >> 32
    R0 = block_message & (2 ** 32 - 1)


    L_last = L0
    R_last = R0

    #now we need to apply all 16 r_keys (using Feistel cipher)
    for i in range(1, 17):
        if decryption:
            i = 17 - i #apply the r_keys in reverse order

        R_next = L_last ^ round_function(R_last, r_keys[i])
        L_next = R_last

        R_last = R_next
        L_last = L_next
       
    #rejoin the 2 halves
    ciphered_block = (R_next << 32) + L_next
    
    #inverse original permutation
    ciphered_block = permute_using_table(IP_INV, ciphered_block, 64)

    return ciphered_block

# Performs Feistal function on key and cipher block
def round_function(Ri, Ki):

    Ri = permute_using_table(E, Ri, 32) #yields 32 -> 48 bits

    Ri = Ri ^ Ki # XOR operation

    #take Ri and split into 8 blocks of 6 bits
    Ri_blocks = [((Ri & (0b111111 << shift)) >> shift) for shift in (42,36,30,24,18,12,6,0)]

    for i, block in enumerate(Ri_blocks):
        #extract the row and col from the block
        row = ((0b100000 & block) >> 4) + (0b1 & block)
        col = (0b011110 & block) >> 1
        Ri_blocks[i] = Sbox[i][16*row+col]

    Ri_blocks = zip(Ri_blocks, (28, 24, 20, 16, 12, 8, 4, 0))
    Ri = 0
    for block, left_shift in Ri_blocks:
        Ri = Ri + (block << left_shift)

    #permutation using P box
    Ri = permute_using_table(P, Ri, 32)

    return Ri

# Given a block, and its length, performs a permutation dependent on the table
def permute_using_table(table, block, block_length):
    string = bin(block)[2:].zfill(block_length)
    permutation = []

    for spot in range(len(table)):
        permutation.append(string[table[spot] -1])

    return int(''.join(permutation), 2)

# helps perform shift on round keys during creation
def left_rotating_func(val, right_bits, max_bits):
    return \
    (val << right_bits%max_bits) & (2**max_bits-1) | \
    ((val & (2**max_bits-1)) >> (max_bits-(right_bits%max_bits)))

# creates the 16 subkeys necessary for DES 
def make_round_keys(C0, D0):
    #need 16 keys
    r_keys = dict.fromkeys(range(0, 17))
    left_rotation = (1,1,2,2,2,2,2,2,1,2,2,2,2,2,2,1)

    C0 = left_rotating_func(C0, 0, 28)
    D0 = left_rotating_func(D0, 0, 28)

    r_keys[0] = (C0, D0)

    #rotate each half-subkey by 1 or 2 bits in order to generate 48 bit subkey
    for i, rotation in enumerate(left_rotation):
        i = i + 1
        Ci = left_rotating_func(r_keys[i-1][0], rotation, 28)
        Di = left_rotating_func(r_keys[i-1][1], rotation, 28)
        r_keys[i] = (Ci, Di)

    del r_keys[0] #this is not one of the keys 

    # combine left and right half-subkeys into full subkey
    for i, (Ci, Di) in r_keys.items():
        Ki = (Ci << 28) + Di
        r_keys[i] = permute_using_table(PC2, Ki, 56) #yields 56 -> 48 bits

    return r_keys


#********** FOR TESTING -------->
#make sure everything starts with "0x"
k = 0x8e6f4d63dea53de65f#0x281dc8a34d3f345d5d#0x0123456789ABCDEF #0x0e329232ea6d0d73 # 64 bit
j = bin(k)
ar = bytearray(int(j, 2).to_bytes((len(j) + 7) // 8, 'big'))


k2 = 0x133457799BBCDFF1
m = 0x8e6f4d63dea53de65f#0x281dc8a34d3f345d5d#0xf7c24f74fa2b167e#0x8787878787878787
n = bin(m)
mar = bytearray(int(n, 2).to_bytes((len(n) + 7) // 8, 'big'))

if __name__ == '__main__':
    enciphered = encipher(ar, mar)
    decipher(ar, enciphered)






    
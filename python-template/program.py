import sys


def encipher(key: bytearray, plaintext: bytearray) -> bytearray:
    return bytearray('success1'.encode('utf8'))


def decipher(key: bytearray, ciphertext: bytearray) -> bytearray:
    return bytearray('success2'.encode('utf8'))


if __name__ == '__main__':
    action, key, text = sys.argv[1:]
    f = encipher if action == 'encipher' else decipher
    output_bytes = f(bytearray.fromhex(key), bytearray.fromhex(text))
    sys.stdout.buffer.write(output_bytes)

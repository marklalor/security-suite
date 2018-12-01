import sys


def encipher(key: str, plaintext: str) -> str:
    return "text1!"


def decipher(key: str, ciphertext: str) -> str:
    return "text2!"


if __name__ == '__main__':
    action, key, text = sys.argv[1:]
    f = encipher if action == 'encipher' else decipher
    print(f(key, text), end='')

def shift_cipher(text, operations):
    ciphered_text = list(text)  # Convert string to a list of characters

    for operation in operations:
        start_idx, end_idx, direction = operation

        for i in range(start_idx, end_idx + 1):
            if direction == 1:
                ciphered_text[i] = chr((ord(ciphered_text[i]) - ord('a') + 1) % 26 + ord('a'))
            else:
                ciphered_text[i] = chr((ord(ciphered_text[i]) - ord('a') + 25) % 26 + ord('a'))

    return ''.join(ciphered_text)  # Convert list of characters back to string

if __name__ == "__main__":
    text = "hello"
    operations = [[0, 1, 1], [2, 3, 0], [0, 2, 1]]

    result = shift_cipher(text, operations)
    print("Ciphered message:", result)

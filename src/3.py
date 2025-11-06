class DES:
    def __init__(self):
        self.IP = [58, 50, 42, 34, 26, 18, 10, 2, 60, 52, 44, 36, 28, 20, 12, 4,
                   62, 54, 46, 38, 30, 22, 14, 6, 64, 56, 48, 40, 32, 24, 16, 8,
                   57, 49, 41, 33, 25, 17, 9, 1, 59, 51, 43, 35, 27, 19, 11, 3,
                   61, 53, 45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 31, 23, 15, 7]

        self.IP_INV = [40, 8, 48, 16, 56, 24, 64, 32, 39, 7, 47, 15, 55, 23, 63, 31,
                       38, 6, 46, 14, 54, 22, 62, 30, 37, 5, 45, 13, 53, 21, 61, 29,
                       36, 4, 44, 12, 52, 20, 60, 28, 35, 3, 43, 11, 51, 19, 59, 27,
                       34, 2, 42, 10, 50, 18, 58, 26, 33, 1, 41, 9, 49, 17, 57, 25]

        self.E_BOX = [32, 1, 2, 3, 4, 5, 4, 5, 6, 7, 8, 9,
                      8, 9, 10, 11, 12, 13, 12, 13, 14, 15, 16, 17,
                      16, 17, 18, 19, 20, 21, 20, 21, 22, 23, 24, 25,
                      24, 25, 26, 27, 28, 29, 28, 29, 30, 31, 32, 1]  # 48位扩展表，输入32位

        self.P_BOX = [16, 7, 20, 21, 29, 12, 28, 17, 1, 15, 23, 26,
                      5, 18, 31, 10, 2, 8, 24, 14, 32, 27, 3, 9,
                      19, 13, 30, 6, 22, 11, 4, 25]  # 32位置换表

        self.PC1 = [57, 49, 41, 33, 25, 17, 9, 1, 58, 50, 42, 34, 26, 18,
                    10, 2, 59, 51, 43, 35, 27, 19, 11, 3, 60, 52, 44, 36,
                    63, 55, 47, 39, 31, 23, 15, 7, 62, 54, 46, 38, 30, 22,
                    14, 6, 61, 53, 45, 37, 29, 21, 13, 5, 28, 20, 12, 4]  # 64→56位置换

        self.PC2 = [14, 17, 11, 24, 1, 5, 3, 28, 15, 6, 21, 10, 23, 19, 12, 4,
                    26, 8, 16, 7, 27, 20, 13, 2, 41, 52, 31, 37, 47, 55, 30, 40,
                    51, 45, 33, 48, 44, 49, 39, 56, 34, 53, 46, 42, 50, 36, 29, 32]  # 56→48位子密钥置换

        self.SHIFT_ROUNDS = [1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1]
        self.S_BOXES = [
            [[14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7],
             [0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8],
             [4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0],
             [15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13]],
            [[15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10],
             [3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5],
             [0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15],
             [13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9]],
            [[10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8],
             [13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1],
             [13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7],
             [1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12]],
            [[7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15],
             [13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9],
             [10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4],
             [3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14]],
            [[2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9],
             [14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6],
             [4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14],
             [11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3]],
            [[12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11],
             [10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8],
             [9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6],
             [4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13]],
            [[4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1],
             [13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6],
             [1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2],
             [6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12]],
            [[13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7],
             [1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2],
             [7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8],
             [2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11]]
        ]

    def _hex_to_bin(self, hex_str):
        """十六进制转64位二进制列表"""
        bin_str = bin(int(hex_str, 16))[2:].zfill(64)
        return list(bin_str)

    def _bin_to_hex(self, bin_list):
        """二进制列表转十六进制字符串"""
        bin_str = ''.join(bin_list)
        return hex(int(bin_str, 2))[2:].zfill(16)

    def _permute(self, data, perm_table):
        """通用置换函数"""
        return [data[i - 1] for i in perm_table]

    def _left_shift(self, data, shift):
        """循环左移"""
        return data[shift:] + data[:shift]

    def generate_subkeys(self, key_hex):
        """生成16个48位子密钥"""
        key_bin = self._hex_to_bin(key_hex)
        key_56 = self._permute(key_bin, self.PC1)
        c, d = key_56[:28], key_56[28:]
        subkeys = []
        for i in range(16):
            c = self._left_shift(c, self.SHIFT_ROUNDS[i])
            d = self._left_shift(d, self.SHIFT_ROUNDS[i])
            subkey = self._permute(c + d, self.PC2)
            subkeys.append(''.join(subkey))
        return subkeys

    def _s_box_substitute(self, expanded):
        """S盒代换：48位→32位"""
        result = []
        for i in range(8):
            block = expanded[i * 6: (i + 1) * 6]
            row = int(block[0] + block[5], 2)
            col = int(''.join(block[1:5]), 2)
            val = self.S_BOXES[i][row][col]
            result.append(bin(val)[2:].zfill(4))
        return ''.join(result)

    def _f_function(self, r, subkey):
        """F函数：32位→32位"""
        # E盒扩展：32位→48位
        r_bin = ''.join(r)
        expanded = self._permute(list(r_bin), self.E_BOX)
        expanded_str = ''.join(expanded)
        # 异或
        xor = [str(int(e) ^ int(k)) for e, k in zip(expanded_str, subkey)]
        # S盒代换
        s_out = self._s_box_substitute(xor)
        # P盒置换
        p_out = self._permute(list(s_out), self.P_BOX)
        return p_out

    def encrypt(self, plaintext_hex, key_hex):
        """DES加密"""
        plain_bin = self._hex_to_bin(plaintext_hex)
        subkeys = self.generate_subkeys(key_hex)
        # 初始置换IP
        ip_result = self._permute(plain_bin, self.IP)
        l, r = ip_result[:32], ip_result[32:]
        # 16轮迭代
        for i in range(16):
            l_prev, r_prev = l.copy(), r.copy()
            f_res = self._f_function(r_prev, subkeys[i])
            r = [str(int(l_prev[j]) ^ int(f_res[j])) for j in range(32)]
            l = r_prev
        # 逆初始置换IP_INV
        final_bin = self._permute(r + l, self.IP_INV)
        return self._bin_to_hex(final_bin)

    def decrypt(self, ciphertext_hex, key_hex):
        """DES解密（子密钥逆序）"""
        cipher_bin = self._hex_to_bin(ciphertext_hex)
        subkeys = self.generate_subkeys(key_hex)[::-1]
        ip_result = self._permute(cipher_bin, self.IP)
        l, r = ip_result[:32], ip_result[32:]
        for i in range(16):
            l_prev, r_prev = l.copy(), r.copy()
            f_res = self._f_function(r_prev, subkeys[i])
            r = [str(int(l_prev[j]) ^ int(f_res[j])) for j in range(32)]
            l = r_prev
        final_bin = self._permute(r + l, self.IP_INV)
        return self._bin_to_hex(final_bin)


# 测试
if __name__ == "__main__":
    des = DES()

    key1 = "0123456789abcdef"
    plain1 = "fedcba9876543210"
    cipher1 = des.encrypt(plain1, key1)
    decrypt1 = des.decrypt(cipher1, key1)

    print(f"明文: {plain1}")
    print(f"密钥: {key1}")
    print(f"加密结果: {cipher1}")



    key2 = "4861496b6e6f7721"
    plain2 = "496c6f7665796f75"
    cipher2 = des.encrypt(plain2, key2)
    decrypt2 = des.decrypt(cipher2, key2)

    print(f"明文: {plain2}")
    print(f"密钥: {key2}")
    print(f"加密结果: {cipher2}")

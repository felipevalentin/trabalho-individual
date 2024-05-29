import java.math.BigInteger;
import java.security.SecureRandom;

public class MillerRabin { // stalings https://oeis.org/A014233
    /*
        * falha para alguns casos com a = BigInteger.TWO, como "2047"
         * Se os restos de (a^q, a^2q, … , a^(2^(k-1)q), a^((2^k)q)) mod n é igual a 1 ou que algum elemento da lista
         * é igual a n-1, caso contrário n é composto.
         * Ele garante, portanto, que não vai ter falso negativo, mas pode ter falso positivo. Ou seja
         * se o teste falhar, n é composto, mas se passar, n é provavelmente primo.
         * Da para aumentar a probailidade de acerto aumentando o número de rodadas. 10 rodadas = 10^-6 de erro segundo stallings
         * Além disso se baseia no fato que qualquer inteiro positivo impar pode ser representado da seguinte forma.
         * n-1 = 2^k * q, onde q é impar e k é um inteiro positivo.
     */

    public static boolean verify(BigInteger n, int t) {
        // 2 e 3 são primos
        if (n.compareTo(BigInteger.TWO) == 0 || n.compareTo(BigInteger.valueOf(3)) == 0)
            return true;

        // Apenas valores maiores que 1
        if (n.compareTo(BigInteger.ONE) <= 0)
            return false;

        // Se for par, não é primo
        if (n.mod(BigInteger.valueOf(2)).compareTo(BigInteger.ZERO) == 0)
            return false;

        // n-1 = 2^k * q
        BigInteger n1 = n.subtract(BigInteger.ONE);
        int k = 0;
        BigInteger q = n1;
        while (q.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            k++;
            q = q.divide(BigInteger.TWO);
        }

        // t rodadas
        boolean prime = true;
        outerloop:
        for (int i = 0; i < t; i++) {

            // a = [2, n-1], numero aleatorio
            SecureRandom secureRandom = new SecureRandom();
            BigInteger a; // a = 2 falha para alguns casos documentados https://oeis.org/A014233
            do
                a = new BigInteger(n1.bitLength(), secureRandom);
            while (a.compareTo(BigInteger.TWO) < 0 || a.compareTo(n1) >= 0);


            // b = a^q mod n
            BigInteger b = a.modPow(q, n);

            // se b == 1 é primo primo
            if (b.equals(BigInteger.ONE))
                continue;

            // b = a^(2^j * q) mod n
            for (int j = 0; j <= k - 1; j++) {
                if (b.equals(n1)) { // b == n-1 é primo
                    continue outerloop;
                } else {
                    b = b.modPow(BigInteger.TWO, n);
                }
            }

            // se não passar em uma rodada, não é primo
            prime = false;
            break;
        }

        return prime;
    }

    public static void main(String[] args) {
        int[] bits = {40, 56, 80, 128, 168, 224, 256, 512, 1024, 2048, 4096};
        long startTime;
        double elapsedTime;
        for (int bit : bits) {
            BigInteger number;
            startTime = System.nanoTime();
            int i = 0;
            do  {
                number = LinearCongruentialGenerator.next(bit);
                i++;
            } while (!verify(number, 10));
            System.out.println(number);
            elapsedTime = ((Long) (System.nanoTime() - startTime)).doubleValue() / 1000000000D;
            System.out.printf("i: %s bits %s: Elapsed time: %s\n",i, bit, elapsedTime);
        }
    }
}

import java.math.BigInteger;
import java.security.SecureRandom;

public class Fermat {
    /*
     * falha para alguns casos com a = BigInteger.TWO, como "2047"
     * Se baseia no pequeno teorema de fermat, se n é primo e a não é divisivel por n, então a^(n-1) é congruente a 1 mod n
     * Ele garante, portanto, que não vai ter falso negativo, mas pode ter falso positivo. Ou seja
     * se o teste falhar, n é composto, mas se passar, n é provavelmente primo.
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

        BigInteger n1 = n.subtract(BigInteger.ONE);
        BigInteger n2 = n.subtract(BigInteger.TWO);

        boolean prime = true;
        for (int i = 0; i < t; i++) {
            // a = [2, n-1], numero aleatorio
            SecureRandom secureRandom = new SecureRandom();
            BigInteger a;
            do
                a = new BigInteger(n2.bitLength(), secureRandom);
            while (a.compareTo(BigInteger.TWO) < 0 || a.compareTo(n2) >= 0);

            // a^(n-1) mod n == 1 se for é possivelmente primo, caso contrário é composto
            prime = a.modPow(n1, n).equals(BigInteger.ONE);
            if (!prime)
                break;
        }
        return prime;
    }

    public static void main(String[] args) {
        BigInteger test = new BigInteger("62967856701149551");
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

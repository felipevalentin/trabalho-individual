import java.math.BigInteger;

public class BlumBlumShub { // Stallings
    /*
        * Blum Blum Shub
        * Gera um tamanho arbitrário de length, pois so é usado o bit menos significativo de cada valor gerado.
        * p e q devem ser primos com n = p * q, onde p e q são primos grandes
        * alem disso p e q devem ser congruentes a 3 mod 4.
        * Após isso é preciso garantir que a seed seja relativamente prima a n.
     */
    private final BigInteger p;
    private final BigInteger q;
    private final BigInteger n;
    private BigInteger seed;

    BlumBlumShub(BigInteger p, BigInteger q, BigInteger seed) {
        if (p.mod(BigInteger.valueOf(4)).compareTo(BigInteger.valueOf(3)) != 0)
            throw new IllegalArgumentException("p must be congruent to 3 mod 4");

        if (q.mod(BigInteger.valueOf(4)).compareTo(BigInteger.valueOf(3)) != 0)
            throw new IllegalArgumentException("q must be congruent to 3 mod 4");

        this.p = p;
        this.q = q;

        // n = p * q
        this.n = this.p.multiply(this.q);

        // é necessário garantir que seed seja relativamente primo a n
        while (!seed.gcd(n).equals(BigInteger.ONE))
            seed = BigInteger.valueOf(System.nanoTime());

        this.seed = seed;
    }

    /*
        gera um número pseudo-aleatório de tamanho length.
        através dos seguintes passos
        1. x0 = seed^2 mod n
        2. xi = xi-1^2 mod n
        3. bit menos significativo de xi é concatenado ao resultado
        4. repete 2 e 3 até que o tamanho do resultado seja maior ou igual a length
     */
    public BigInteger generate(int length) {
        // x0 = seed^2 mod n
        seed = seed.modPow(BigInteger.TWO, n);

        BigInteger result = BigInteger.ZERO;

        while (result.bitLength() < length) {
            // xi = xi-1^2 mod n
            seed = seed.modPow(BigInteger.TWO, n);

            // bit menos significativo
            BigInteger least = seed.mod(BigInteger.TWO);

            // concatena o bit menos significativo
            result = result.shiftLeft(1).or(least);
        }

        return result;
    }

    public static BigInteger next(int length){
        BigInteger p = new BigInteger("30000000091");
        BigInteger q = new BigInteger("40000000003");
        BlumBlumShub bbs = new BlumBlumShub(p, q, BigInteger.valueOf(System.nanoTime()));
        return bbs.generate(length);
    }

    public static void main(String[] args) {
        int[] lengths = {40, 56, 80, 128, 168, 224, 256, 512, 1024, 2048, 4096};
        long startTime, elapsedTime;
        for (int length : lengths) {
            startTime = System.nanoTime();
            for (int i = 0; i < 10; i++) {
                BigInteger number = next(length);
//                System.out.printf("%s, %s\n", number.bitLength(), number);
            }
            elapsedTime = (System.nanoTime() - startTime)/10;
            System.out.printf("Bit size: %s, Average Elapsed time: %s\n", length, elapsedTime);
        }
    }
}
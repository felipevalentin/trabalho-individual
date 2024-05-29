import java.math.BigInteger;

public class LinearCongruentialGenerator {
    /*
    * Linear congruential generator
    * Consegue gerar números pseudo-aleatórios até modulo m, m pode ser arbitrariamente grande, como 2^4096.
    * O problema é que se a,c e m não forem bem escolhidos o gerador pode ser previsível, ou seja, não aleatório.
    * Além disso, o gerador pode ter periodo curto e não possuir um valor de um certo grupo de bits, pois
    * não é garantido o número de estados gerados para valores de a,c e m quaisquer.
    * O ideal é usar valores conhecidos e testados como os usados mais abaixo de
    * Numerical Recipes ranqd1, Chapter 7.1, §An Even Quicker Generator, Eq. 7.1.6 parameters from Knuth and H. W. Lewis.
     */
    private final BigInteger m;
    private final BigInteger a;
    private final BigInteger c;
    private BigInteger seed;

    public LinearCongruentialGenerator(BigInteger m, BigInteger a, BigInteger c, BigInteger seed) {
        if (m.compareTo(BigInteger.ZERO) <= 0)
            throw new IllegalArgumentException("invalid m");

        if (a.compareTo(BigInteger.ZERO) <= 0 || a.compareTo(m) >= 0)
            throw new IllegalArgumentException("invalid a");

        if (c.compareTo(BigInteger.ZERO) < 0 || c.compareTo(m) >= 0)
            throw new IllegalArgumentException("invalid c");

        if (seed.compareTo(BigInteger.ZERO) < 0 || seed.compareTo(m) >= 0)
            throw new IllegalArgumentException("invalid seed");

        this.m = m;
        this.a = a;
        this.c = c;
        this.seed = seed;
    }

    /*
        * Gera o próximo número pseudo-aleatório.
        * O próximo número é gerado a partir do número anterior, multiplicando por a e somando c, tudo mod m.
     */
    public BigInteger generate() {
        seed = (a.multiply(seed).add(c)).mod(m);
        return seed;
    }

    /*
        * Gera um número pseudo-aleatório de tamanho 2^length.
        * Para gerar um número aleatório de tamanho arbitrário usando um lcg de 32 bits é necessário concatenar vários
        * números de 32 bits ou menos. Para isso a estratégia usada foi dividir o tamanho desejado por 32 e pegar o resto da
        * divisão e o quociente. O resto da divisão é a quantidade de bits do primeiro número gerado e o quociente é a
        * quantidade de números de 32 bits que serão gerados.
        * A partir dai é só fazer um shift left de 32 bits e or com o próximo número gerado, or é a operação de
        * concatenação de bits.
     */
    public static BigInteger next(int length) {
        BigInteger seed = BigInteger.valueOf((int) (System.nanoTime() & Integer.MAX_VALUE));
        BigInteger a = new BigInteger("1664525");
        BigInteger c = new BigInteger("1013904223");
        BigInteger m = new BigInteger("2").pow(32);
        LinearCongruentialGenerator lcg = new LinearCongruentialGenerator(m, a, c, seed);


        int quotient = length / 32;
        int remainder = length % 32; // ex: 40 % 32 = 8
        BigInteger result = BigInteger.ZERO;
        BigInteger val;

        // pode adicionar algum nivel de vies de modulo, uma alternativa é tratar isso confome discutido abaixo
        // https://stackoverflow.com/questions/10984974/why-do-people-say-there-is-modulo-bias-when-using-a-random-number-generator
        if (remainder != 0)
            while (result.bitLength() < remainder) // gera até ter o tamanho 2^remainder
                result = lcg.generate().mod(BigInteger.TWO.pow(remainder));


        for (int i = 0; i < quotient; i++) {
            do
                val = lcg.generate();
            while (val.bitLength() != 32); // gera até ter o tamanho de 32 bits

            result = result.shiftLeft(32).or(val);
        }

        return result;
    }

    public static void main(String[] args) {
        int[] lengths = {40, 56, 80, 128, 168, 224, 256, 512, 1024, 2048, 4096};
        long startTime, elapsedTime;
        for (int length : lengths) {
            startTime = System.nanoTime();
            for (int i = 0; i < 50; i++) {
                BigInteger number = next(length);
//                System.out.printf("%s, %s\n", number.bitLength(), number);
            }
            elapsedTime = (System.nanoTime() - startTime)/10;
            System.out.printf("Bit size: %s, Average Elapsed time: %s\n", length, elapsedTime);
        }
    }
}
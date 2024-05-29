public class extra {
    /*
     * Gera um número pseudo-aleatório menor que upperBound.
     * Aqui se usa do mod, mas evitando mod bias.
     * https://docs.oracle.com/javase/7/docs/api/java/util/Random.html#nextInt%28%29
     * https://stackoverflow.com/questions/10984974/why-do-people-say-there-is-modulo-bias-when-using-a-random-number-generator
     * https://github.com/openbsd/src/blob/master/lib/libc/crypt/arc4random_uniform.c
     */
//    private static BigInteger nextWithUpperBound(BigInteger upperBound) {
//        BigInteger bits;
//        BigInteger val;
//
//        do {
//            bits = next();
//            val = bits.mod(upperBound);
//        } while (bits.subtract(val).add(upperBound.subtract(BigInteger.ONE)).compareTo(BigInteger.ZERO) < 0);
//
//        return val;
//    }
}

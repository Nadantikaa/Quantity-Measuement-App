public class mesurementapp {

    static class Feet {
        private final double value;

        public Feet(double value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Feet other = (Feet) obj;
            return Double.compare(this.value, other.value) == 0;
        }
    }

    static class Inches {
        private final double value;

        public Inches(double value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Inches other = (Inches) obj;
            return Double.compare(this.value, other.value) == 0;
        }
    }

    public static boolean compareFeet(double a, double b) {
        return new Feet(a).equals(new Feet(b));
    }

    public static boolean compareInches(double a, double b) {
        return new Inches(a).equals(new Inches(b));
    }

    public static void main(String[] args) {
        System.out.println(compareInches(1.0, 1.0));
        System.out.println(compareFeet(1.0, 1.0));
    }
}
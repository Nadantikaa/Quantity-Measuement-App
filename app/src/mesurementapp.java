public class mesurementapp {

    enum LengthUnit {
        FEET(1.0),
        INCH(1.0 / 12.0),
        YARDS(3.0),
        CENTIMETERS(0.393701 / 12.0);

        private final double toFeet;

        LengthUnit(double toFeet) {
            this.toFeet = toFeet;
        }

        public double toBase(double value) {
            return value * toFeet;
        }

        public double fromBase(double valueInFeet) {
            return valueInFeet / toFeet;
        }
    }

    static class QuantityLength {
        private final double value;
        private final LengthUnit unit;

        public QuantityLength(double value, LengthUnit unit) {
            if (!Double.isFinite(value) || unit == null) throw new IllegalArgumentException();
            this.value = value;
            this.unit = unit;
        }

        private double toFeet() {
            return unit.toBase(value);
        }

        public QuantityLength convertTo(LengthUnit target) {
            if (target == null) throw new IllegalArgumentException();
            double base = toFeet();
            double converted = target.fromBase(base);
            return new QuantityLength(converted, target);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            QuantityLength other = (QuantityLength) obj;
            return Double.compare(this.toFeet(), other.toFeet()) == 0;
        }

        @Override
        public String toString() {
            return value + " " + unit;
        }
    }

    public static double convert(double value, LengthUnit source, LengthUnit target) {
        if (!Double.isFinite(value) || source == null || target == null) throw new IllegalArgumentException();
        double base = source.toBase(value);
        return target.fromBase(base);
    }

    public static void main(String[] args) {
        System.out.println(convert(1.0, LengthUnit.FEET, LengthUnit.INCH));
        System.out.println(convert(3.0, LengthUnit.YARDS, LengthUnit.FEET));
        System.out.println(convert(36.0, LengthUnit.INCH, LengthUnit.YARDS));
        System.out.println(convert(1.0, LengthUnit.CENTIMETERS, LengthUnit.INCH));
        System.out.println(new QuantityLength(1.0, LengthUnit.FEET).convertTo(LengthUnit.INCH));
    public static void main(String[] args) {
        System.out.println(new QuantityLength(1.0, LengthUnit.YARDS).equals(new QuantityLength(3.0, LengthUnit.FEET)));
        System.out.println(new QuantityLength(1.0, LengthUnit.YARDS).equals(new QuantityLength(36.0, LengthUnit.INCH)));
        System.out.println(new QuantityLength(1.0, LengthUnit.CENTIMETERS).equals(new QuantityLength(0.393701, LengthUnit.INCH)));
    }
}
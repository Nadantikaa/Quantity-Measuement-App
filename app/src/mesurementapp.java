interface IMeasurable {
    double getConversionFactor();
    double convertToBaseUnit(double value);
    double convertFromBaseUnit(double baseValue);
    String getUnitName();
}

enum LengthUnit implements IMeasurable {
    FEET(1.0),
    INCH(1.0 / 12.0),
    YARDS(3.0),
    CENTIMETERS(0.393701 / 12.0);

    private final double factor;

    LengthUnit(double factor) { this.factor = factor; }

    public double getConversionFactor() { return factor; }
    public double convertToBaseUnit(double value) { return value * factor; }
    public double convertFromBaseUnit(double baseValue) { return baseValue / factor; }
    public String getUnitName() { return name(); }
}

enum WeightUnit implements IMeasurable {
    KILOGRAM(1.0),
    GRAM(0.001),
    POUND(0.453592);

    private final double factor;

    WeightUnit(double factor) { this.factor = factor; }

    public double getConversionFactor() { return factor; }
    public double convertToBaseUnit(double value) { return value * factor; }
    public double convertFromBaseUnit(double baseValue) { return baseValue / factor; }
    public String getUnitName() { return name(); }
}

enum VolumeUnit implements IMeasurable {
    LITRE(1.0),
    MILLILITRE(0.001),
    GALLON(3.78541);

    private final double factor;

    VolumeUnit(double factor) { this.factor = factor; }

    public double getConversionFactor() { return factor; }
    public double convertToBaseUnit(double value) { return value * factor; }
    public double convertFromBaseUnit(double baseValue) { return baseValue / factor; }
    public String getUnitName() { return name(); }
}

public class mesurementapp {

    static class Quantity<U extends IMeasurable> {
        private final double value;
        private final U unit;
        private static final double EPSILON = 1e-6;

        private enum ArithmeticOperation {
            ADD((a, b) -> a + b),
            SUBTRACT((a, b) -> a - b),
            DIVIDE((a, b) -> {
                if (b == 0) throw new ArithmeticException();
                return a / b;
            });

            private final java.util.function.DoubleBinaryOperator op;

            ArithmeticOperation(java.util.function.DoubleBinaryOperator op) {
                this.op = op;
            }

            double compute(double a, double b) {
                return op.applyAsDouble(a, b);
            }
        }

        public Quantity(double value, U unit) {
            if (!Double.isFinite(value) || unit == null) throw new IllegalArgumentException();
            this.value = value;
            this.unit = unit;
        }

        private double toBase() {
            return unit.convertToBaseUnit(value);
        }

        private void validate(Quantity<U> other, U target, boolean needTarget) {
            if (other == null) throw new IllegalArgumentException();
            if (!Double.isFinite(other.value)) throw new IllegalArgumentException();
            if (unit.getClass() != other.unit.getClass()) throw new IllegalArgumentException();
            if (needTarget && target == null) throw new IllegalArgumentException();
        }

        private double performBaseArithmetic(Quantity<U> other, ArithmeticOperation op) {
            validate(other, null, false);
            return op.compute(this.toBase(), other.toBase());
        }

        private double round(double v) {
            return Math.round(v * 100.0) / 100.0;
        }

        public Quantity<U> convertTo(U target) {
            if (target == null) throw new IllegalArgumentException();
            return new Quantity<>(target.convertFromBaseUnit(toBase()), target);
        }

        public Quantity<U> add(Quantity<U> other) {
            return add(other, this.unit);
        }

        public Quantity<U> add(Quantity<U> other, U target) {
            validate(other, target, true);
            double result = performBaseArithmetic(other, ArithmeticOperation.ADD);
            return new Quantity<>(round(target.convertFromBaseUnit(result)), target);
        }

        public Quantity<U> subtract(Quantity<U> other) {
            return subtract(other, this.unit);
        }

        public Quantity<U> subtract(Quantity<U> other, U target) {
            validate(other, target, true);
            double result = performBaseArithmetic(other, ArithmeticOperation.SUBTRACT);
            return new Quantity<>(round(target.convertFromBaseUnit(result)), target);
        }

        public double divide(Quantity<U> other) {
            validate(other, null, false);
            return performBaseArithmetic(other, ArithmeticOperation.DIVIDE);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Quantity<?> other = (Quantity<?>) obj;
            if (unit.getClass() != other.unit.getClass()) return false;
            return Math.abs(this.toBase() - other.unit.convertToBaseUnit(other.value)) < EPSILON;
        }

        @Override
        public int hashCode() {
            long bits = Double.doubleToLongBits(toBase());
            return (int) (bits ^ (bits >>> 32));
        }

        @Override
        public String toString() {
            return value + " " + unit.getUnitName();
        }
    }

    public static void main(String[] args) {
        Quantity<LengthUnit> a = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> b = new Quantity<>(6.0, LengthUnit.INCH);

        System.out.println(a.add(b));
        System.out.println(a.subtract(b));
        System.out.println(a.divide(new Quantity<>(2.0, LengthUnit.FEET)));
        Quantity<LengthUnit> l1 = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> l2 = new Quantity<>(6.0, LengthUnit.INCH);
        System.out.println(l1.subtract(l2));
        System.out.println(l1.divide(new Quantity<>(2.0, LengthUnit.FEET)));

        Quantity<WeightUnit> w1 = new Quantity<>(10.0, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> w2 = new Quantity<>(5000.0, WeightUnit.GRAM);
        System.out.println(w1.subtract(w2));
        System.out.println(w1.divide(new Quantity<>(5.0, WeightUnit.KILOGRAM)));

        Quantity<VolumeUnit> v1 = new Quantity<>(5.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 = new Quantity<>(500.0, VolumeUnit.MILLILITRE);
        System.out.println(v1.subtract(v2));
        System.out.println(v1.divide(new Quantity<>(10.0, VolumeUnit.LITRE)));
        Quantity<VolumeUnit> v1 = new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 = new Quantity<>(1000.0, VolumeUnit.MILLILITRE);
        Quantity<VolumeUnit> v3 = new Quantity<>(1.0, VolumeUnit.GALLON);

        System.out.println(v1.equals(v2));
        System.out.println(v1.convertTo(VolumeUnit.MILLILITRE));
        System.out.println(v1.add(v2, VolumeUnit.LITRE));
        System.out.println(v3.convertTo(VolumeUnit.LITRE));
        System.out.println(v1.add(v3, VolumeUnit.MILLILITRE));
        Quantity<LengthUnit> l1 = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> l2 = new Quantity<>(12.0, LengthUnit.INCH);
        System.out.println(l1.equals(l2));
        System.out.println(l1.convertTo(LengthUnit.INCH));
        System.out.println(l1.add(l2, LengthUnit.FEET));

        Quantity<WeightUnit> w1 = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> w2 = new Quantity<>(1000.0, WeightUnit.GRAM);
        System.out.println(w1.equals(w2));
        System.out.println(w1.convertTo(WeightUnit.GRAM));
        System.out.println(w1.add(w2, WeightUnit.KILOGRAM));
        System.out.println(new QuantityWeight(1.0, WeightUnit.KILOGRAM).equals(new QuantityWeight(1000.0, WeightUnit.GRAM)));
        System.out.println(new QuantityWeight(1.0, WeightUnit.KILOGRAM).convertTo(WeightUnit.POUND));
        System.out.println(QuantityWeight.add(new QuantityWeight(1.0, WeightUnit.KILOGRAM), new QuantityWeight(1000.0, WeightUnit.GRAM), WeightUnit.GRAM));
        System.out.println(new QuantityLength(1.0, LengthUnit.FEET).convertTo(LengthUnit.INCH));
        System.out.println(QuantityLength.add(new QuantityLength(1.0, LengthUnit.FEET), new QuantityLength(12.0, LengthUnit.INCH), LengthUnit.FEET));
        System.out.println(new QuantityLength(36.0, LengthUnit.INCH).equals(new QuantityLength(1.0, LengthUnit.YARDS)));
        System.out.println(QuantityLength.add(new QuantityLength(1.0, LengthUnit.FEET), new QuantityLength(12.0, LengthUnit.INCH), LengthUnit.INCH));
        System.out.println(QuantityLength.add(new QuantityLength(1.0, LengthUnit.FEET), new QuantityLength(12.0, LengthUnit.INCH), LengthUnit.YARDS));
        System.out.println(new QuantityLength(1.0, LengthUnit.FEET).add(new QuantityLength(12.0, LengthUnit.INCH)));
        System.out.println(new QuantityLength(12.0, LengthUnit.INCH).add(new QuantityLength(1.0, LengthUnit.FEET)));
        System.out.println(QuantityLength.add(new QuantityLength(1.0, LengthUnit.YARDS), new QuantityLength(3.0, LengthUnit.FEET), LengthUnit.YARDS));
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
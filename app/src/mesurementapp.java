enum LengthUnit {
    FEET(1.0),
    INCH(1.0 / 12.0),
    YARDS(3.0),
    CENTIMETERS(0.393701 / 12.0);

    private final double toFeet;

    LengthUnit(double toFeet) {
        this.toFeet = toFeet;
    }

    public double convertToBaseUnit(double value) {
        return value * toFeet;
    }

    public double convertFromBaseUnit(double baseValue) {
        return baseValue / toFeet;
    }
}

enum WeightUnit {
    KILOGRAM(1.0),
    GRAM(0.001),
    POUND(0.453592);

    private final double toKg;

    WeightUnit(double toKg) {
        this.toKg = toKg;
    }

    public double convertToBaseUnit(double value) {
        return value * toKg;
    }

    public double convertFromBaseUnit(double baseValue) {
        return baseValue / toKg;
    }
}

public class mesurementapp {

    static class QuantityLength {
        private final double value;
        private final LengthUnit unit;

        public QuantityLength(double value, LengthUnit unit) {
            if (!Double.isFinite(value) || unit == null) throw new IllegalArgumentException();
            this.value = value;
            this.unit = unit;
        }

        public QuantityLength convertTo(LengthUnit target) {
            if (target == null) throw new IllegalArgumentException();
            double base = unit.convertToBaseUnit(value);
            double converted = target.convertFromBaseUnit(base);
            return new QuantityLength(converted, target);
        }

        public static QuantityLength add(QuantityLength a, QuantityLength b, LengthUnit target) {
            if (a == null || b == null || target == null) throw new IllegalArgumentException();
            double sum = a.unit.convertToBaseUnit(a.value) + b.unit.convertToBaseUnit(b.value);
            return new QuantityLength(target.convertFromBaseUnit(sum), target);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            QuantityLength other = (QuantityLength) obj;
            return Double.compare(unit.convertToBaseUnit(value), other.unit.convertToBaseUnit(other.value)) == 0;
        }

        @Override
        public String toString() {
            return value + " " + unit;
        }
    }

    static class QuantityWeight {
        private final double value;
        private final WeightUnit unit;

        public QuantityWeight(double value, WeightUnit unit) {
            if (!Double.isFinite(value) || unit == null) throw new IllegalArgumentException();
            this.value = value;
            this.unit = unit;
        }

        public QuantityWeight convertTo(WeightUnit target) {
            if (target == null) throw new IllegalArgumentException();
            double base = unit.convertToBaseUnit(value);
            double converted = target.convertFromBaseUnit(base);
            return new QuantityWeight(converted, target);
        }

        public QuantityWeight add(QuantityWeight other) {
            if (other == null) throw new IllegalArgumentException();
            return add(this, other, this.unit);
        }

        public static QuantityWeight add(QuantityWeight a, QuantityWeight b, WeightUnit target) {
            if (a == null || b == null || target == null) throw new IllegalArgumentException();
            double sum = a.unit.convertToBaseUnit(a.value) + b.unit.convertToBaseUnit(b.value);
            return new QuantityWeight(target.convertFromBaseUnit(sum), target);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            QuantityWeight other = (QuantityWeight) obj;
            return Double.compare(unit.convertToBaseUnit(value), other.unit.convertToBaseUnit(other.value)) == 0;
        }

        @Override
        public String toString() {
            return value + " " + unit;
        }
    }

    public static void main(String[] args) {
        System.out.println(new QuantityWeight(1.0, WeightUnit.KILOGRAM).equals(new QuantityWeight(1000.0, WeightUnit.GRAM)));
        System.out.println(new QuantityWeight(1.0, WeightUnit.KILOGRAM).convertTo(WeightUnit.POUND));
        System.out.println(QuantityWeight.add(new QuantityWeight(1.0, WeightUnit.KILOGRAM), new QuantityWeight(1000.0, WeightUnit.GRAM), WeightUnit.GRAM));
    }
}
package myUtility;

public class ElementIndex implements Comparable {
        private double value;
        private int index;

        public ElementIndex(double a, int i){
            value=a;
            index=i;
        }

        /**
         * @return Returns the value.
         */
        public double getValue() {
            return value;
        }

        /**
         * @return Returns the index.
         */
        public int getIndex() {
            return index;
        }

        public int compareTo(Object o) {
            if (this.value == ((ElementIndex) o).value)
                return 0;
            else if (this.value > ((ElementIndex) o).value)
                return 1;
            else
                return -1;   
        }
    }
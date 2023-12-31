package com.mwin.reward.async.models.number_sudoku_puzzle;

import androidx.annotation.NonNull;

import java.util.HashSet;
import java.util.Random;

public class GameMatrix {
    private static final String ROW_SEPARATOR = ";";
    private static final String COL_SEPARATOR = ",";
    private int[][] matrix;
    private int size, emptyCellRow, emptyCellCol;

    /**
     * make game matrix of given size
     *
     * @param size size of matrix
     */
    public GameMatrix(int size) {
        HandleInvalid.size(size);
        this.size = size;
        this.matrix = new int[size][size];
        fillSeriesMatrix();
        shuffleMatrix();
        validateMatrix();
    }

    /**
     * make game matrix from matrix
     *
     * @param matrix matrix
     */
    private GameMatrix(int[][] matrix) {
        HandleInvalid.matrix(matrix);
        this.matrix = matrix;
        this.size = matrix.length;
        validateMatrix();
    }

    /**
     * make game matrix from array
     *
     * @param arr array
     */
    public GameMatrix(int[] arr) {
        HandleInvalid.array(arr);
        int size = (int) Math.sqrt(arr.length);
        this.matrix = new int[size][size];
        this.size = size;
        for (int i = 0; i < size; i++) {
            System.arraycopy(arr, (i * size), this.matrix[i], 0, size);
        }
        validateMatrix();
    }

    /**
     * make game matrix from string
     *
     * @param matrixString matrix as a string
     */
    public GameMatrix(String matrixString) {
        this(matrixString, false);
    }

    /**
     * make game matrix from string
     *
     * @param matrixString matrix string obtained by toString method
     * @param formatted    string formatted or not
     */
    private GameMatrix(String matrixString, boolean formatted) {
        try {
            if (formatted) {
                String[] matrixStringRows = matrixString.split(ROW_SEPARATOR);

                int size = matrixStringRows.length;

                this.matrix = new int[size][size];
                this.size = size;

                for (int i = 0; i < size; ++i) {
                    String[] matrixStringColumns = matrixStringRows[i].split(COL_SEPARATOR);
                    for (int j = 0; j < size; ++j) {
                        set(i, j, Integer.parseInt(matrixStringColumns[j]));
                    }
                }
            } else {
                String[] matrixStringArray = matrixString.split(COL_SEPARATOR);

                int size = (int) Math.sqrt(matrixStringArray.length);

                this.matrix = new int[size][size];
                this.size = size;

                for (int i = 0; i < size; ++i) {
                    for (int j = 0; j < size; ++j) {
                        set(i, j, Integer.parseInt(matrixStringArray[i * size + j]));
                    }
                }
            }
            HandleInvalid.matrix(this.matrix);
            validateMatrix();
        } catch (Exception e) {
            throw new Error("Invalid matrix string");
        }
    }

    /**
     * Calculate number of inversions in the matrix
     *
     * @return number of inversions
     */
    private int getInversions() {
        int arr[] = getArray();
        int count = 0;
        for (int i = 0; i < this.size * this.size; i++) {
            if (arr[i] == 0) continue;
            for (int j = i + 1; j < this.size * this.size; j++) {
                if (arr[j] != 0 && arr[j] < arr[i]) count++;
            }
        }
        return count;
    }

    public int getSize() {
        return this.size;
    }

    public int getEmptyCellRow() {
        return this.emptyCellRow;
    }

    public int getEmptyCellCol() {
        return this.emptyCellCol;
    }

    /**
     * Fill the matrix in ascending order from 1, and 0 at last
     */
    private void fillSeriesMatrix() {
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                set(i, j, (i * this.size + j + 1) % (this.size * this.size));
            }
        }
    }

    /**
     * shuffle a matrix in random order
     */
    private void shuffleMatrix() {
        int pos_x = this.size - 1, pos_y = this.size - 1;
        int temp, temp_x, temp_y;

        Random rand = new Random();

        for (int index = this.size * this.size - 1; index > 1; index--) {
            temp = rand.nextInt(index);
            temp_x = temp / this.size;
            temp_y = (temp + this.size) % this.size;
            swap(temp_x, temp_y, pos_x, pos_y);

            if (pos_y == 0) {
                pos_x--;
                pos_y = this.size - 1;
            } else {
                pos_y--;
            }
        }
    }

    @NonNull
    @Override
    public String toString() {
        return toString(false);
    }

    /**
     * Check if the matrix is valid according to following rules:
     * If n is even, then the matrix is solvable if:
     * 1. blank is on even row counting from the bottom and no of inversions is odd.
     * 2. blank is on odd row from the bottom and no of inversions is even.
     * If n is odd, then the matrix is solvable if no. of inversions is even.
     *
     * @return validity of matrix
     */
    private boolean isValid() {
        int inv = getInversions();
        int emptyCellRowFromBottom = this.size - getEmptyCellRow();

        return (emptyCellRowFromBottom % 2 == 0 && inv % 2 != 0) || (emptyCellRowFromBottom % 2 != 0 && inv % 2 == 0);
    }

    /**
     * If puzzle is not solvable, make it solvable by decreasing one inversion
     * which can be done easily by swapping two last positions
     */
    private void validateMatrix() {
        if (isValid()) {
            return;
        }

        int n1 = this.size - 1;
        int n2 = this.size - 2;
        int n3 = this.size - 3;

        if (!isEmpty(n1, n1)) {
            if (!isEmpty(n1, n2)) {
                swap(n1, n2, n1, n1);
            } else {
                swap(n1, n3, n1, n1);
            }
        } else {
            swap(n1, n3, n1, n2);
        }
    }

    /**
     * swap elements at given positions
     *
     * @param row1 element 1 row
     * @param col1 element 1 column
     * @param row2 element 2 row
     * @param col2 element 2 column
     */
    public void swap(int row1, int col1, int row2, int col2) {
        int temp = get(row1, col1);
        set(row1, col1, get(row2, col2));
        set(row2, col2, temp);
    }

    /**
     * get element at given position
     *
     * @param row element row
     * @param col element column
     * @return element
     */
    public int get(int row, int col) {
        return this.matrix[row][col];
    }

    /**
     * set element at given position
     *
     * @param row   element row
     * @param col   element column
     * @param value element value
     */
    private void set(int row, int col, int value) {
        this.matrix[row][col] = value;

        if (isEmpty(row, col)) {
            emptyCellRow = row;
            emptyCellCol = col;
        }
    }

    /**
     * @return matrix is solved or not
     */
    public boolean isSolved() {
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                if (get(i, j) != (i * this.size + j + 1) % (this.size * this.size))
                    return false;
            }
        }
        return true;
    }

    /**
     * @param formatted rows and columns are separated by different delimiters or by same one.
     * @return game matrix string
     */
    public String toString(boolean formatted) {
        StringBuilder stringBuilder = new StringBuilder();
        if (formatted) {
            for (int i = 0; i < this.size; ++i) {
                if (i != 0) stringBuilder.append(ROW_SEPARATOR);
                for (int j = 0; j < this.size; ++j) {
                    if (j != 0) stringBuilder.append(COL_SEPARATOR);
                    stringBuilder.append(get(i, j));
                }
            }
            return stringBuilder.toString();
        } else {
            int[] array = getArray();
            stringBuilder.append(array[0]);
            for (int i = 1; i < array.length; ++i) {
                stringBuilder.append(COL_SEPARATOR);
                stringBuilder.append(array[i]);
            }
        }
        return stringBuilder.toString();
    }

    /**
     * @return flattened 1D matrix
     */
    private int[] getArray() {
        int[] arr = new int[(this.size * this.size)];
        for (int i = 0; i < this.size; i++) {
            System.arraycopy(this.matrix[i], 0, arr, i * this.size, this.size);
        }
        return arr;
    }

    /**
     * @param row    element row
     * @param column element column
     * @return element at given row and column is empty(zero) or not
     */
    public boolean isEmpty(int row, int column) {
        return get(row, column) == 0;
    }

    private static class HandleInvalid {
        static final int MIN_SIZE = 3;

        /**
         * check size
         *
         * @param size size
         */
        static void size(int size) {
            if (size < MIN_SIZE) {
                throw new Error("Size should not be less than " + MIN_SIZE);
            }
        }

        /**
         * check matrix
         *
         * @param matrix matrix
         */
        static void matrix(int[][] matrix) {
            int size = matrix.length;
            HandleInvalid.size(size);

            HashSet<Integer> set = new HashSet<>();
            for (int[] row : matrix) {
                if (row.length != size) throw new Error("Matrix is not a square matrix");

                for (int element : row) {
                    if (set.contains(element)) {
                        throw new Error("Matrix contains duplicate element " + element);
                    }
                    set.add(element);
                }
            }
            for (int i = 0; i < size * size; ++i) {
                if (!set.contains(i)) throw new Error("Matrix doesn't contains " + i);
            }
        }

        /**
         * check array
         *
         * @param arr array
         */
        static void array(int[] arr) {
            double size = Math.sqrt(arr.length);
            if (size % 1 != 0)
                throw new Error("Length of array must be a perfect square");

            HashSet<Integer> set = new HashSet<>();
            for (int element : arr) {
                if (set.contains(element)) {
                    throw new Error("Array contains duplicate element " + element);
                }
                set.add(element);
            }
            for (int i = 0; i < size * size; ++i) {
                if (!set.contains(i)) throw new Error("Array doesn't contain " + i);
            }
        }
    }
}

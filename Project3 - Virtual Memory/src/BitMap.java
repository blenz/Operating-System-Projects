/**
 * Created by blenz on 3/9/17.
 */
public class BitMap {

    private int[] bm, mask1, mask2;
    private final int size;

    public BitMap() {
        size = 32;
        bm = new int[size];
        mask1 = new int[size];
        mask2 = new int[size];

        for (int i = 0,j = size-1; i < size; i++,j--)
            mask1[j] = (int)Math.pow(2,i);

        for (int i = 0; i < size; i++)
            mask2[i] = ~mask1[i];


//
//        for (int i = 0; i < size; i++) {
//            System.out.println(i+ " " +Integer.toBinaryString(mask1[i]));
//        }

//        for (int i = 0; i < size; i++) {
//            System.out.println(i+ " " +Integer.toBinaryString(mask2[i]));
//        }
    }

    public void setBitToZero(int j){
        bm[j] = bm[j] & mask2[j];
    }

    public void setBitToOne(int j){
        bm[j] = bm[j] | mask2[j%size];
    }
    
    public int search(){

        int test;

        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++) {
                test = bm[i] & mask1[j];
                if (test == 0)
                    return (i*size)+j;
            }
        return -1;
    }

    public void printBM(){
        for (int i = 0; i < size; i++) {
            System.out.println(Integer.toBinaryString(bm[i]));
        }
        System.out.println();
    }
}

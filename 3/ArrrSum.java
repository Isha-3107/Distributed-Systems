import mpi.MPI;

public class ArrrSum {
    public static void main(String[] args) throws Exception {
        MPI.Init(args);

        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        int unitsize = 5;
        int root = 0;

        int[] sendBuffer = null;
        int[] receiveBuffer = new int[unitsize];
        int[] gatheredResults = new int[size];

        if (rank == root) {
            sendBuffer = new int[unitsize * size];
            System.out.println("Enter " + sendBuffer.length + " elements:");
            for (int i = 0; i < sendBuffer.length; i++) {
                sendBuffer[i] = i + 1; // Fill array with values (1 to N)
                System.out.println("Element " + i + ": " + sendBuffer[i]);
            }
        }

        // Scatter the data to all processes
        MPI.COMM_WORLD.Scatter(sendBuffer, 0, unitsize, MPI.INT, receiveBuffer, 0, unitsize, MPI.INT, root);

        // Calculate the sum in each process (rank)
        int localSum = 0;
        for (int num : receiveBuffer) {
            localSum += num;
        }
        System.out.println("Intermediate sum at process " + rank + ": " + localSum);

        // Gather results at the root process
        MPI.COMM_WORLD.Gather(new int[] { localSum }, 0, 1, MPI.INT, gatheredResults, 0, 1, MPI.INT, root);

        // Final sum calculation at root process
        if (rank == root) {
            int finalSum = 0;
            for (int sum : gatheredResults) {
                finalSum += sum;
            }
            System.out.println("Final sum: " + finalSum);
        }

        MPI.Finalize();
    }
}
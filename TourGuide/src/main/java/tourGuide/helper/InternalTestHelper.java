package tourGuide.helper;

public class InternalTestHelper {

    // Set this default up to 100,000 for testing
    private static int internalUserNumber = 50000;

    public static int getInternalUserNumber() {

        return internalUserNumber;
    }

    public static void setInternalUserNumber(int internalUserNumber) {

        InternalTestHelper.internalUserNumber = internalUserNumber;
    }
}

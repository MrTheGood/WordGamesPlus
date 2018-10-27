package eu.insertcode.wordgames.util

import org.junit.Test

class UpdateCheckerKtTest {

    @Test
    fun newerThan() {
        assert("1.0.0" newerThan "0.1.0")
        assert("1.2.0" newerThan "1.1.0")
        assert("1.2.1" newerThan "1.2.0")
        assert("1.1.beta" newerThan "1.0.beta")
        assert("1.1.beta1" newerThan "1.1.beta")
        assert("1.1.1" newerThan "1.1.beta")
    }


}
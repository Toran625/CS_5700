import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested

class DetectorTest {

    @Nested
    @DisplayName("Integer Detector Tests")
    inner class IntegerDetectorTests {
        private val detector = IntegerDetector()

        @Test
        @DisplayName("Valid integers should be detected")
        fun testValidIntegers() {
            val validIntegers = listOf("1", "123", "3452342352434534524346", "9", "456789")
            validIntegers.forEach { input ->
                assertTrue(detector.detect(input), "Expected '$input' to be a valid integer")
            }
        }

        @Test
        @DisplayName("Invalid integers should be rejected")
        fun testInvalidIntegers() {
            val invalidIntegers = listOf(
                "", "0123", "132a", "0", "01", "12a3", "12 3", "12.3", "-123", "+123"
            )
            invalidIntegers.forEach { input ->
                assertFalse(detector.detect(input), "Expected '$input' to be an invalid integer")
            }
        }

        @Test
        @DisplayName("Edge cases")
        fun testEdgeCases() {
            assertFalse(detector.detect(""), "Empty string should be invalid")
            assertFalse(detector.detect("0"), "Single zero should be invalid")
            assertTrue(detector.detect("1"), "Single non-zero digit should be valid")
        }
    }

    @Nested
    @DisplayName("Float Detector Tests")
    inner class FloatDetectorTests {
        private val detector = FloatDetector()

        @Test
        @DisplayName("Valid floats should be detected")
        fun testValidFloats() {
            val validFloats = listOf(
                "1.0", "123.34", "0.20000", "12349871234.12340981234098",
                ".123", "9.9", "0.0", "123.000", ".0", "999.999"
            )
            validFloats.forEach { input ->
                assertTrue(detector.detect(input), "Expected '$input' to be a valid float")
            }
        }

        @Test
        @DisplayName("Invalid floats should be rejected")
        fun testInvalidFloats() {
            val invalidFloats = listOf(
                "123", "123.123.", "123.02a", "123.", "012.4", "", ".",
                "12.34.56", "abc.123", "12a.34", "12.3a4", "-12.34", "12 .34"
            )
            invalidFloats.forEach { input ->
                assertFalse(detector.detect(input), "Expected '$input' to be an invalid float")
            }
        }

        @Test
        @DisplayName("Float starting with period")
        fun testPeriodStart() {
            assertTrue(detector.detect(".123"), "Float starting with period should be valid")
            assertFalse(detector.detect("."), "Just period should be invalid")
        }

        @Test
        @DisplayName("Zero handling")
        fun testZeroHandling() {
            assertTrue(detector.detect("0.123"), "0.123 should be valid")
            assertFalse(detector.detect("012.4"), "012.4 should be invalid (leading zero not followed by period)")
        }
    }

    @Nested
    @DisplayName("Binary Detector Tests")
    inner class BinaryDetectorTests {
        private val detector = BinaryDetector()

        @Test
        @DisplayName("Valid binary numbers should be detected")
        fun testValidBinary() {
            val validBinary = listOf(
                "1", "11", "101", "111111", "10011010001", "111",
                "1001", "10101", "11111111111"
            )
            validBinary.forEach { input ->
                assertTrue(detector.detect(input), "Expected '$input' to be valid binary")
            }
        }

        @Test
        @DisplayName("Invalid binary numbers should be rejected")
        fun testInvalidBinary() {
            val invalidBinary = listOf(
                "01", "10", "1000010", "100a01", "0", "00", "110",
                "0110", "1 1", "12", "", "1a1"
            )
            invalidBinary.forEach { input ->
                assertFalse(detector.detect(input), "Expected '$input' to be invalid binary")
            }
        }

        @Test
        @DisplayName("Must start and end with 1")
        fun testStartEndWith1() {
            assertTrue(detector.detect("1"), "Single 1 should be valid")
            assertTrue(detector.detect("101"), "101 should be valid")
            assertFalse(detector.detect("01"), "Should not start with 0")
            assertFalse(detector.detect("10"), "Should not end with 0")
        }

        @Test
        @DisplayName("Only binary digits allowed")
        fun testOnlyBinaryDigits() {
            assertTrue(detector.detect("10101"), "Valid binary sequence")
            assertFalse(detector.detect("12101"), "Should reject non-binary digits")
            assertFalse(detector.detect("1a1"), "Should reject letters")
        }
    }

    @Nested
    @DisplayName("Email Detector Tests")
    inner class EmailDetectorTests {
        private val detector = EmailDetector()

        @Test
        @DisplayName("Valid emails should be detected")
        fun testValidEmails() {
            val validEmails = listOf(
                "a@b.c", "joseph.ditton@usu.edu", "test@example.com",
                "user123@domain.org", "x@y.z",
                "special!chars@domain.info"
            )
            validEmails.forEach { input ->
                assertTrue(detector.detect(input), "Expected '$input' to be a valid email")
            }
        }

        @Test
        @DisplayName("Invalid emails should be rejected")
        fun testInvalidEmails() {
            val invalidEmails = listOf(
                "@b.c", "a@b@c.com", "a.b@b.b.c", "joseph ditton@usu.edu",
                "a@b.", "a@.c", "abc", "a@b", "a.b.c", "", "@", "a@",
                "a@b@", "a@@b.c", " @b.c", "a@ .c", "a@b. ", "long.email.address@very.long.domain.name"
            )
            invalidEmails.forEach { input ->
                assertFalse(detector.detect(input), "Expected '$input' to be an invalid email")
            }
        }

        @Test
        @DisplayName("Email format requirements")
        fun testEmailFormat() {
            // Must have exactly one @ symbol
            assertTrue(detector.detect("user@domain.com"), "Valid format")
            assertFalse(detector.detect("user@@domain.com"), "Multiple @ symbols")
            assertFalse(detector.detect("userdomain.com"), "No @ symbol")

            // Must have exactly one period after @
            assertTrue(detector.detect("user@domain.com"), "One period after @")
            assertFalse(detector.detect("user@domain..com"), "Multiple periods after @")
            assertFalse(detector.detect("user@domain"), "No period after @")
        }

        @Test
        @DisplayName("No spaces allowed")
        fun testNoSpaces() {
            assertTrue(detector.detect("user@domain.com"), "No spaces - valid")
            assertFalse(detector.detect("user @domain.com"), "Space in part1")
            assertFalse(detector.detect("user@ domain.com"), "Space in part2")
            assertFalse(detector.detect("user@domain .com"), "Space in part3")
        }
    }

    @Nested
    @DisplayName("Password Detector Tests")
    inner class PasswordDetectorTests {
        private val detector = PasswordDetector()

        @Test
        @DisplayName("Valid passwords should be detected")
        fun testValidPasswords() {
            val validPasswords = listOf(
                "aaaaH!aa", "1234567*9J", "asdpoihj;loikjasdf;ijp;lij2309jasd;lfkm20ij@aH",
                "!Password123", "MySecure&Pass", "Complex#1234",
                "SuperLong\$PasswordWithManyChars", "Test@123a", "Hello!World1A"
            )
            validPasswords.forEach { input ->
                assertTrue(detector.detect(input), "Expected '$input' to be a valid password")
            }
        }

        @Test
        @DisplayName("Invalid passwords should be rejected")
        fun testInvalidPasswords() {
            val invalidPasswords = listOf(
                "a", "aaaaaaa!", "aaaHaaaaa", "Abbbbbbb!", "shortA!",
                "lowercase!123", "UPPERCASE123", "Password123*", "NoSpecial123A",
                "noupperca\$e", "A!a", "", "12345678", "PASSWORD!", "Pass Word@"
            )
            invalidPasswords.forEach { input ->
                assertFalse(detector.detect(input), "Expected '$input' to be an invalid password")
            }
        }

        @Test
        @DisplayName("Password length requirement")
        fun testPasswordLength() {
            assertFalse(detector.detect("A!a"), "Less than 8 characters should be invalid")
            assertFalse(detector.detect("Short!A"), "7 characters should be invalid")
            assertTrue(detector.detect("LongEn!A"), "8 characters should be valid")
        }

        @Test
        @DisplayName("Must have uppercase letter")
        fun testUppercaseRequirement() {
            assertTrue(detector.detect("Password!123"), "Has uppercase")
            assertFalse(detector.detect("password!123"), "No uppercase")
        }

        @Test
        @DisplayName("Must have special character")
        fun testSpecialCharRequirement() {
            val specialChars = listOf("!", "@", "#", "$", "%", "&", "*")
            specialChars.forEach { special ->
                assertTrue(detector.detect("Password${special}123"), "Should accept special char '$special'")
            }
            assertFalse(detector.detect("Password123"), "No special character")
        }

        @Test
        @DisplayName("Cannot end with special character")
        fun testCannotEndWithSpecial() {
            assertTrue(detector.detect("Password!123"), "Special char in middle is valid")
            assertFalse(detector.detect("Password123!"), "Cannot end with special char")
            assertFalse(detector.detect("Password123@"), "Cannot end with @")
            assertFalse(detector.detect("Password123*"), "Cannot end with *")
        }

        @Test
        @DisplayName("Complex password requirements")
        fun testComplexRequirements() {
            // Must have all: length >= 8, uppercase, special char, not end with special
            assertTrue(detector.detect("MyPass!123"), "Meets all requirements")
            assertFalse(detector.detect("mypass!123"), "Missing uppercase")
            assertFalse(detector.detect("MyPass123"), "Missing special char")
            assertFalse(detector.detect("MyP!"), "Too short")
            assertFalse(detector.detect("MyPass123!"), "Ends with special char")
        }
    }

    @Nested
    @DisplayName("State Machine Behavior Tests")
    inner class StateMachineTests {

        @Test
        @DisplayName("Detector state resets between calls")
        fun testStateReset() {
            val detector = IntegerDetector()

            // First call with invalid input
            assertFalse(detector.detect("abc"))

            // Second call with valid input should work (state should reset)
            assertTrue(detector.detect("123"))

            // Third call with invalid input
            assertFalse(detector.detect("12a"))

            // Fourth call with valid input should work
            assertTrue(detector.detect("456"))
        }

        @Test
        @DisplayName("State transitions work correctly")
        fun testStateTransitions() {
            val detector = BinaryDetector()

            // Test that detector properly transitions through states
            assertTrue(detector.detect("101"), "Should handle alternating binary")
            assertTrue(detector.detect("1"), "Should handle single character")
            assertTrue(detector.detect("11111"), "Should handle repeated characters")

            // Test rejection at different points
            assertFalse(detector.detect("01"), "Should reject at start")
            assertFalse(detector.detect("10"), "Should reject at end")
            assertFalse(detector.detect("1a1"), "Should reject in middle")
        }
    }
}
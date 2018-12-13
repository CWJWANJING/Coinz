//package com.example.wanjing.coinz;
//import android.support.annotation.StringRes;
//import android.support.test.runner.AndroidJUnit4;
//import android.view.View;
//
//import org.hamcrest.Matcher;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import static android.support.test.espresso.Espresso.onView;
//import static android.support.test.espresso.action.ViewActions.clearText;
//import static android.support.test.espresso.action.ViewActions.click;
//import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
//import static android.support.test.espresso.action.ViewActions.typeText;
//import static android.support.test.espresso.assertion.ViewAssertions.matches;
//
//import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
//import static android.support.test.espresso.matcher.ViewMatchers.withId;
//import static android.support.test.espresso.matcher.ViewMatchers.withText;
//
//
//@RunWith(AndroidJUnit4.class)
//public class LoginEspressoTest {
//
//    @Rule
//    public ActivityTestRule<firebaseLogin> mActivityRule =
//            new ActivityTestRule<>(firebaseLogin.class);
//
//    @Test
//    public void emailIsEmpty() {
//        onView(withId(R.id.email)).perform(clearText());
//        onView(withId(R.id.SignIn)).perform(click());
//        onView(withId(R.id.email)).check(matches(withError(getString(R.string.error_field_required))));
//    }
//
//    @Test
//    public void passwordIsEmpty() {
//        onView(withId(R.id.email)).perform(typeText("email@email.com"), closeSoftKeyboard());
//        onView(withId(R.id.pass)).perform(clearText());
//        onView(withId(R.id.SignIn)).perform(click());
//        onView(withId(R.id.pass)).check(matches(withError(getString(R.string.error_field_required))));
//    }
//
//    @Test
//    public void emailIsInvalid() {
//        onView(withId(R.id.email)).perform(typeText("invalid"), closeSoftKeyboard());
//        onView(withId(R.id.SignIn)).perform(click());
//        onView(withId(R.id.email)).check(matches(withError(getString(R.string.error_invalid_email))));
//    }
//
//    @Test
//    public void passwordIsTooShort() {
//        onView(withId(R.id.email)).perform(typeText("email@email.com"), closeSoftKeyboard());
//        onView(withId(R.id.pass)).perform(typeText("1234"), closeSoftKeyboard());
//        onView(withId(R.id.SignIn)).perform(click());
//        onView(withId(R.id.pass)).check(matches(withError(getString(R.string.error_invalid_password))));
//    }
//
//
//    @Test
//    public void loginSuccessfully_shouldShowWelcomeMessage() {
//        onView(withId(R.id.email)).perform(typeText("user@email.com"), closeSoftKeyboard());
//        onView(withId(R.id.pass)).perform(typeText("123456"), closeSoftKeyboard());
//        onView(withId(R.id.SignIn)).perform(click());
//    }
//
//    @Test
//    public void loginSuccessfully_shouldShowToast() {
//        onView(withId(R.id.email)).perform(typeText("user@email.com"), closeSoftKeyboard());
//        onView(withId(R.id.pass)).perform(typeText("123456"), closeSoftKeyboard());
//        onView(withId(R.id.SignIn)).perform(click());
//    }
//
//    private String getString(@StringRes int resourceId) {
//        return activityTestRule.getActivity().getString(resourceId);
//    }
//
//    private static Matcher<? super View> withError(final String expected) {
//        return new TypeSafeMatcher<View>() {
//            @Override
//            protected boolean matchesSafely(View item) {
//                if (item instanceof EditText) {
//                    return ((EditText)item).getError().toString().equals(expected);
//                }
//                return false;
//            }
//
//            @Override
//            public void describeTo(Description description) {
//                description.appendText("Not found error message" + expected + ", find it!");
//            }
//        };
//    }
//}

package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({RequestTest.class, ResponseTest.class, BatchTest.class, ClientServerTest.class})
public class AllTest {
}

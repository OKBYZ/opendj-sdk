/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at legal-notices/CDDLv1_0.txt
 * or http://forgerock.org/license/CDDLv1.0.html.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at legal-notices/CDDLv1_0.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information:
 *      Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 *
 *      Copyright 2009 Sun Microsystems, Inc.
 *      Portions Copyright 2014 ForgeRock AS
 */
package org.opends.server.core.networkgroups;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.opends.server.DirectoryServerTestCase;
import org.opends.server.TestCaseUtils;
import org.opends.server.api.ClientConnection;
import org.opends.server.authorization.dseecompat.PatternDN;
import org.opends.server.types.AuthenticationType;
import org.opends.server.types.DN;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Unit tests for BindDNConnectionCriteria.
 */
public class BindDNConnectionCriteriaTest extends
    DirectoryServerTestCase
{

  /**
   * Sets up the environment for performing the tests in this suite.
   *
   * @throws Exception
   *           if the environment could not be set up.
   */
  @BeforeClass
  public void setUp() throws Exception
  {
    TestCaseUtils.startServer();
  }



  /**
   * Returns test data for the following test cases.
   *
   * @return The test data for the following test cases.
   * @throws Exception
   *           If an unexpected exception occurred.
   */
  @DataProvider(name = "testData")
  public Object[][] createTestData() throws Exception
  {
    DN anonymousUser = DN.rootDN();
    DN rootUser =
        DN.valueOf("cn=Directory Manager, cn=Root DNs, cn=config");
    PatternDN rootMatch = PatternDN.decode("*, cn=Root DNs, cn=config");
    PatternDN rootNoMatch =
        PatternDN.decode("cn=Dirx*, cn=Root DNs, cn=config");

    return new Object[][] {
        { anonymousUser, Collections.<PatternDN> emptySet(), false },
        { rootUser, Collections.<PatternDN> emptySet(), false },
        { anonymousUser, Collections.singleton(rootMatch), false },
        { rootUser, Collections.singleton(rootMatch), true },
        { anonymousUser, Collections.singleton(rootNoMatch), false },
        { rootUser, Collections.singleton(rootNoMatch), false },
        { anonymousUser, Arrays.asList(rootMatch, rootNoMatch), false },
        { rootUser, Arrays.asList(rootMatch, rootNoMatch), true }, };
  }



  /**
   * Tests the matches method.
   *
   * @param clientBindDN
   *          The client bind DN.
   * @param allowedDNPatterns
   *          The set of allowed DN patterns.
   * @param expectedResult
   *          The expected result.
   * @throws Exception
   *           If an unexpected exception occurred.
   */
  @Test(dataProvider = "testData")
  public void testMatches(DN clientBindDN,
      Collection<PatternDN> allowedDNPatterns, boolean expectedResult)
      throws Exception
  {
    ClientConnection client = new MockClientConnection(12345, false, clientBindDN);

    BindDNConnectionCriteria criteria = new BindDNConnectionCriteria(new ArrayList<PatternDN>(allowedDNPatterns));
    assertEquals(criteria.matches(client), expectedResult);
  }



  /**
   * Tests the willMatchAfterBind method.
   *
   * @param clientBindDN
   *          The client bind DN.
   * @param allowedDNPatterns
   *          The set of allowed DN patterns.
   * @param expectedResult
   *          The expected result.
   * @throws Exception
   *           If an unexpected exception occurred.
   */
  @Test(dataProvider = "testData")
  public void testWillMatchAfterBind(DN clientBindDN,
      Collection<PatternDN> allowedDNPatterns, boolean expectedResult)
      throws Exception
  {
    ClientConnection client = new MockClientConnection(12345, false, null);

    BindDNConnectionCriteria criteria = new BindDNConnectionCriteria(new ArrayList<PatternDN>(allowedDNPatterns));
    assertEquals(criteria.willMatchAfterBind(client, clientBindDN,
            AuthenticationType.SIMPLE, false), expectedResult);
  }

}

/*******************************************************************************
 * Copyright (c) 2012 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.xpect.xtext.lib.tests;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.List;

import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.validation.CheckMode;
import org.eclipse.xtext.validation.Issue;
import org.junit.runner.RunWith;
import org.xpect.expectation.ILinesExpectation;
import org.xpect.expectation.LinesExpectation;
import org.xpect.runner.Xpect;
import org.xpect.runner.XpectRunner;
import org.xpect.setup.XpectSetup;
import org.xpect.xtext.lib.setup.ThisOffset;
import org.xpect.xtext.lib.setup.ThisResource;
import org.xpect.xtext.lib.setup.XtextStandaloneSetup;
import org.xpect.xtext.lib.tests.ValidationTestModuleSetup.TestingResourceValidator;
import org.xpect.xtext.lib.util.IssueFormatter;
import org.xpect.xtext.lib.util.IssueOverlapsRangePredicate;

/**
 * @author Moritz Eysholdt - Initial contribution and API
 */
@RunWith(XpectRunner.class)
@XpectSetup({ XtextStandaloneSetup.class, ValidationTestModuleSetup.class })
public class ValidationTest {

	@Xpect
	public void errors(@LinesExpectation ILinesExpectation expectation, @ThisResource XtextResource resource, @ThisOffset int offset) {
		List<String> issues = validate(resource, offset, Severity.ERROR);
		expectation.assertEquals(issues);
	}

	@Xpect
	public void infos(@LinesExpectation ILinesExpectation expectation, @ThisResource XtextResource resource, @ThisOffset int offset) {
		List<String> issues = validate(resource, offset, Severity.INFO);
		expectation.assertEquals(issues);
	}

	@Xpect
	public void issues(@LinesExpectation ILinesExpectation expectation, @ThisResource XtextResource resource, @ThisOffset int offset) {
		List<String> issues = validate(resource, offset, null);
		expectation.assertEquals(issues);
	}

	protected List<String> validate(XtextResource resource, int offset, Severity severity) {
		TestingResourceValidator validator = (TestingResourceValidator) resource.getResourceServiceProvider().getResourceValidator();
		Collection<Issue> allIssues = validator.unfilteredValidate(resource, CheckMode.ALL, CancelIndicator.NullImpl);
		List<Issue> issuesInLine = newArrayList(filter(allIssues, new IssueOverlapsRangePredicate(resource, offset, severity)));
		List<String> formattedIssues = newArrayList(transform(issuesInLine, new IssueFormatter(resource, false)));
		return formattedIssues;
	}

	@Xpect
	public void warnings(@LinesExpectation ILinesExpectation expectation, @ThisResource XtextResource resource, @ThisOffset int offset) {
		List<String> issues = validate(resource, offset, Severity.WARNING);
		expectation.assertEquals(issues);
	}
}

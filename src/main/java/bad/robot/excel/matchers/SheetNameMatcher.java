/*
 * Copyright (c) 2012-2013, bad robot (london) ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package bad.robot.excel.matchers;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.ArrayList;
import java.util.List;

import static bad.robot.excel.sheet.SheetIterable.sheetsOf;
import static bad.robot.excel.sheet.SheetNameIterable.sheetNamesOf;
import static java.lang.String.format;

/**
 * For every sheet in the "expected", the matcher checks to see if a sheet of the same name is in the "actual".
 *
 * The "actual" can contain more sheets however.
 */
public class SheetNameMatcher extends TypeSafeDiagnosingMatcher<Workbook> {

    private final Workbook expected;

    private SheetNameMatcher(Workbook expected) {
        this.expected = expected;
    }

    public static SheetNameMatcher containsSameNamedSheetsAs(Workbook expected) {
        return new SheetNameMatcher(expected);
    }

    @Override
    protected boolean matchesSafely(Workbook actual, Description mismatch) {
        List<String> missingSheets = new ArrayList<String>();
        for (Sheet sheet : sheetsOf(expected)) {
            if (actual.getSheet(sheet.getSheetName()) == null)
                missingSheets.add(sheet.getSheetName());
        }
        mismatch.appendValueList("sheet(s) ", ", ", notFound(missingSheets), missingSheets);
        return missingSheets.isEmpty();
    }

    @Override
    public void describeTo(Description description) {
        if (!anyPreviousDescriptionsIncludedIn(description))
            description.appendText("workbook to contain sheets ");
        description.appendText("named ").appendValueList("", ", ", "", sheetNamesOf(expected));
    }

    private static boolean anyPreviousDescriptionsIncludedIn(Description description) {
        return !description.toString().endsWith("Expected: ");
    }

    private static String notFound(List<String> values) {
        return format(" %s missing", values.size() == 1 ? "was" : "were");
    }
}

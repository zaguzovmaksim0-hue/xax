/*
 * SPDX-FileCopyrightText: Copyright (c) 2022-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.eolang.xax;

import com.jcabi.matchers.XhtmlMatchers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link Xtory}.
 * @since 0.1.0
 */
final class XtoryTest {

    @Test
    void loadsStylesheetFromEncodedFileUri(final @TempDir Path temp) throws Exception {
        final Path dir = Files.createDirectories(temp.resolve("with space"));
        final Path sheet = dir.resolve("sheet.xsl");
        Files.write(
            sheet,
            String.join(
                "",
                "<xsl:stylesheet version='1.0' ",
                "xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>",
                "<xsl:template match='/doc'><changed/></xsl:template>",
                "</xsl:stylesheet>"
            ).getBytes(StandardCharsets.UTF_8)
        );
        final Xtory xtory = new XtYaml(
            String.join(
                System.lineSeparator(),
                "sheets:",
                String.format("  - %s", sheet.toUri()),
                "document: <doc/>",
                "asserts:",
                "  - /changed"
            )
        );
        MatcherAssert.assertThat(
            "File URI stylesheet was not applied",
            XhtmlMatchers.xhtml(xtory.after()),
            XhtmlMatchers.hasXPath("/changed")
        );
    }

    @Test
    void parsesAndTransforms() throws Exception {
        final Xtory xtory = new XtYaml(
            new TextOf(
                new ResourceOf("org/eolang/xax/packs/simple.yaml")
            ).asString()
        );
        MatcherAssert.assertThat(
            "Fails to find expected XPaths",
            XhtmlMatchers.xhtml(xtory.after()),
            XhtmlMatchers.hasXPaths(xtory.asserts())
        );
    }
}

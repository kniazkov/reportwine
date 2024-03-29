/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Polina Volkhontseva
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.cqfn.reportwine;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;
import com.haulmont.yarg.structure.BandData;
import java.io.File;
import java.io.IOException;
import org.cqfn.reportwine.converters.IrToYargConverter;
import org.cqfn.reportwine.converters.YamlToIrConverter;
import org.cqfn.reportwine.exceptions.BaseException;
import org.cqfn.reportwine.generators.DocxGenerator;
import org.cqfn.reportwine.model.IrMerger;
import org.cqfn.reportwine.model.Pair;
import org.cqfn.reportwine.utils.FileNameValidator;

/**
 * Main class.
 *
 * @since 0.1
 */
public class Main {
    /**
     * The template file.
     */
    @Parameter(
        names = { "--template", "-t" },
        converter = FileConverter.class,
        validateWith = FileNameValidator.class,
        required = true,
        arity = 1,
        description = "The file with template document. Expected extensions: .docx"
    )
    private File template;

    /**
     * The file to store the generated report.
     */
    @Parameter(
        names = { "--output", "-o" },
        validateWith = FileNameValidator.class,
        required = true,
        arity = 1,
        description = "The name (path) of the generated report with extension. Supported: .docx"
    )
    private File output;

    /**
     * The project file.
     */
    @Parameter(
        names = { "--project", "-p" },
        converter = FileConverter.class,
        required = true,
        arity = 1,
        description = "The file with project description. Expected extension: .yml"
    )
    private File project;

    /**
     * The configuration file.
     */
    @Parameter(
        names = { "--config", "-c" },
        converter = FileConverter.class,
        arity = 1,
        description = "The file with configuration settings. Expected extension: .yml"
    )
    private File config;

    /**
     * The help option.
     */
    @Parameter(names = "--help", help = true)
    private boolean help;

    /**
     * The main function. Parses the command line and runs actions.
     * @param args The command-line arguments
     * @throws IOException If an error during input or output actions occurs
     * @throws BaseException If an error during a document processing occurs
     */
    public static void main(final String... args) throws BaseException, IOException {
        final Main main = new Main();
        final JCommander jcr = JCommander.newBuilder()
            .addObject(main)
            .build();
        jcr.parse(args);
        if (main.help) {
            jcr.usage();
            return;
        }
        main.run();
    }

    /**
     * Runs actions.
     * @throws IOException If an error during input or output actions occurs
     * @throws BaseException If an error during a document processing occurs
     */
    private void run() throws IOException, BaseException {
        final Pair info = Main.convertYamlToIr(this.project);
        final IrToYargConverter converter;
        if (this.config == null) {
            converter = new IrToYargConverter(info);
        } else {
            final Pair settings = Main.convertYamlToIr(this.config);
            final IrMerger merger = new IrMerger();
            final Pair combined = merger.merge(info, settings);
            converter = new IrToYargConverter(combined);
        }
        final BandData mappings = converter.convert();
        final DocxGenerator generator = new DocxGenerator(mappings);
        generator.renderDocument(this.template, this.output);
    }

    /**
     * Converts the YAML data into the intermediate representation.
     * @param file The YAML file
     * @return The intermediate representation of the project data
     * @throws IOException If an error during input or output actions occurs
     * @throws BaseException If an error during a document processing occurs
     */
    private static Pair convertYamlToIr(final File file) throws IOException, BaseException {
        final YamlMapping yaml = Yaml.createYamlInput(file).readYamlMapping();
        final YamlToIrConverter conv = new YamlToIrConverter(yaml);
        return conv.convert();
    }
}

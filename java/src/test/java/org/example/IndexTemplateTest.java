package org.example;

import com.samskivert.mustache.Mustache;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStreamReader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IndexTemplateTest {
    @Test
    void indexIsSoundHtml() {
        var model = new TodoList();

        var html = renderTemplate("/index.tmpl", model);

        assertSoundHtml(html);
    }

    @Test
    void todoItemsAreShown() throws IOException {
        var model = new TodoList();
        model.add("Foo");
        model.add("Bar");

        var html = renderTemplate("/index.tmpl", model);

        var document = parseHtml(html);
        var selection = document.select("ul.todo-list li");
        assertThat(selection).hasSize(2);
        assertThat(selection.get(0).text()).isEqualTo("Foo");
        assertThat(selection.get(1).text()).isEqualTo("Bar");
    }

    @Test
    void completedItemsGetCompletedClass() {
        var model = new TodoList();
        model.add("Foo");
        model.addCompleted("Bar");

        var html = renderTemplate("/index.tmpl", model);

        var document = parseHtml(html);
        var selection = document.select("ul.todo-list li.completed");
        assertThat(selection).hasSize(1);
        assertThat(selection.text()).isEqualTo("Bar");
    }

    private static Document parseHtml(String html) {
        return Jsoup.parse(html, "");
    }

    // thanks https://stackoverflow.com/a/64465867/164802
    private static void assertSoundHtml(String html) {
        var parser = Parser.htmlParser().setTrackErrors(10);
        Jsoup.parse(html, "", parser);
        assertThat(parser.getErrors()).isEmpty();
    }

    @SuppressWarnings("DataFlowIssue")
    private String renderTemplate(String templateName, Object model) {
        var template = Mustache.compiler().compile(
                new InputStreamReader(
                        getClass().getResourceAsStream(templateName)));
        return template.execute(model);
    }
}

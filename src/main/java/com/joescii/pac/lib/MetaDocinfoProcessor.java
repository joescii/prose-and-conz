package com.joescii.pac.lib;

import org.asciidoctor.ast.Document;
import org.asciidoctor.extension.DocinfoProcessor;

import java.util.Map;

public class MetaDocinfoProcessor extends DocinfoProcessor {

    public MetaDocinfoProcessor() {
        super();
    }

    public MetaDocinfoProcessor(Map<String, Object> config) {
        super(config);
    }

    @Override
    public String process(Document document) {
        Object published = document.getAttributes().get("published");
        Object updated = document.getAttributes().get("updated");
        updated = updated == null ? published : updated;

        String meta =
            "<meta name=\"published\" content=\""+published+"\">" +
            "<meta name=\"updated\" content=\""+updated+"\">";

        return meta;
    }
}
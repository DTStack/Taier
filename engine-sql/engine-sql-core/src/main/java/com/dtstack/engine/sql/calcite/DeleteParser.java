package com.dtstack.engine.sql.calcite;

import com.dtstack.engine.sql.node.Identifier;
import com.dtstack.engine.sql.node.Node;
import javafx.util.Pair;

import java.util.List;

public class DeleteParser extends LineageParser {
    @Override
    public List<Pair<Identifier, Identifier>> parseColumnLineage(Node node) {
        return null;
    }

    @Override
    public List<Pair<Identifier, Identifier>> parseTableLineage(Node node) {
        return null;
    }
}

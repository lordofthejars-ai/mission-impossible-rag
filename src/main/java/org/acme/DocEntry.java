package org.acme;

import java.util.List;

public record DocEntry(String text, String id, List<Object> vector) {
}

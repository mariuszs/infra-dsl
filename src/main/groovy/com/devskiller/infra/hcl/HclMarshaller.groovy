package com.devskiller.infra.hcl

class HclMarshaller {

	static String provider(String provider) {
		return new BlockBuilder().append("provider \"$provider\" {\n}")
	}

	static String resource(String type, String name, Map<String, Object> properties) {
		return processElement('resource', type, name, properties)
	}

	static String data(String type, String name, Map<String, Object> properties) {
		return processElement('data', type, name, properties)
	}

	private static String processElement(String elementType, String type, String name, Map<String, Object> properties) {
		BlockBuilder builder = new BlockBuilder()
		builder.addEmptyLine()
				.addLine("$elementType \"$type\" \"${HclUtil.escapeResourceName(name)}\" {")
				.startBlock()

		processProperties(builder, properties)

		builder.endBlock()
				.addLine("}")

	}

	private static void processProperties(BlockBuilder builder, Map<String, Object> properties) {
		properties.each {
			entry ->
				if (entry.value instanceof FlatList) {
					entry.value.each {
						singleValue ->
							builder.addEmptyLine()
							builder.addLine("$entry.key {")
							builder.startBlock()
							processProperties(builder, singleValue as Map)
							builder.endBlock()
							builder.addLine("}")
					}
				} else if (entry.value instanceof Map) {
					builder.addEmptyLine()
					builder.addLine("$entry.key {")
					builder.startBlock()
					processProperties(builder, entry.value as Map)
					builder.endBlock()
					builder.addLine("}")
				} else {
					if (entry.value != null) {
						builder.addProperty(entry.key, entry.value)
					}
				}
		}
	}
}

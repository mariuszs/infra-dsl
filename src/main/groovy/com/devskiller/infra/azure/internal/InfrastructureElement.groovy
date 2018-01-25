package com.devskiller.infra.azure.internal

import com.devskiller.infra.azure.ResourceGroup
import com.devskiller.infra.hcl.HclMarshaller

abstract class InfrastructureElement {

	protected final ResourceGroup resourceGroup

	private final String resourceType

	private final String resourceName

	protected InfrastructureElement(ResourceGroup resourceGroup, String resourceType, String name = null) {
		this.resourceGroup = resourceGroup
		this.resourceType = resourceType
		this.resourceName = name
	}

	protected abstract Map getAsMap()

	String renderElement() {
		HclMarshaller.resource(resourceType,
				elementName(),
				elementProperties())
	}

	Map elementProperties() {
		commonProperties() + getAsMap() + resourceGroup.getCommonTags(resourceName)
	}

	private Map<String, String> commonProperties() {
		[
				'name'               : elementName(),
				'resource_group_name': resourceGroup.getResourceQualifier(ResourceGroup.class),
				'location'           : resourceGroup.region
		]
	}

	private String elementName() {
		resourceGroup.getResourceQualifier(this.class, resourceName ?: [] as String[])
	}

}

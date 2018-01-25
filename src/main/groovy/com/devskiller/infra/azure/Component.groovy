package com.devskiller.infra.azure

import com.devskiller.infra.azure.resource.AvailabilitySet
import com.devskiller.infra.azure.internal.DslContext
import com.devskiller.infra.azure.internal.InfrastructureElementCollection

class Component extends InfrastructureElementCollection {

	String name
	List<ComponentElement> entries = []

	Component(ResourceGroup resourceGroup, String name) {
		super(resourceGroup)
		this.name = name
	}

	void availabilitySet(@DelegatesTo(AvailabilitySet) Closure closure) {
		entries << DslContext.create(new AvailabilitySet(resourceGroup, name), closure)
	}

}
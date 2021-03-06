package com.devskiller.infra.azure

import com.devskiller.infra.azure.internal.DslContext
import com.devskiller.infra.azure.internal.InfrastructureElement
import com.devskiller.infra.azure.internal.InfrastructureElementCollection
import com.devskiller.infra.azure.resource.AvailabilitySet
import com.devskiller.infra.azure.resource.CosmosDB
import com.devskiller.infra.azure.resource.LoadBalancer
import com.devskiller.infra.azure.resource.MachineSet
import com.devskiller.infra.azure.resource.NetworkSecurityGroup
import com.devskiller.infra.azure.resource.PublicIp

class Component extends InfrastructureElementCollection {

	String name
	List<InfrastructureElement> entries = []

	Component(ResourceGroup resourceGroup, String name) {
		super(resourceGroup)
		this.name = name
	}

	void availabilitySet(@DelegatesTo(AvailabilitySet) Closure closure) {
		entries << DslContext.create(new AvailabilitySet(resourceGroup, name), closure)
	}

	void publicIp(@DelegatesTo(PublicIp) Closure closure) {
		entries << DslContext.create(new PublicIp(resourceGroup, name), closure)
	}

	void networkSecurityGroup(@DelegatesTo(NetworkSecurityGroup) Closure closure) {
		entries << DslContext.create(new NetworkSecurityGroup(resourceGroup, name), closure)
	}

	void loadBalancer(@DelegatesTo(LoadBalancer) Closure closure) {
		entries << DslContext.create(
				new LoadBalancer(resourceGroup, name,
						findDependantElement(PublicIp)),
				closure)
	}

	void cosmosDB(@DelegatesTo(CosmosDB) Closure closure) {
		entries << DslContext.create(new CosmosDB(resourceGroup, name), closure)
	}

	void virtualMachines(@DelegatesTo(MachineSet) Closure closure) {
		entries << DslContext.create(
				new MachineSet(resourceGroup, name,
						findDependantElement(NetworkSecurityGroup),
						findDependantElement(LoadBalancer),
						findDependantElement(AvailabilitySet)
				),
				closure)
	}

	private <T> T findDependantElement(Class<T> elementClass) {
		entries.find { (it.getClass() == elementClass) } as T
	}

}

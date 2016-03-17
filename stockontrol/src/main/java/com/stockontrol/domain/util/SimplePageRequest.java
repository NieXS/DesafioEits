package com.stockontrol.domain.util;

import java.io.Serializable;

import org.directwebremoting.annotations.DataTransferObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

/**
 * Abstração simplificada de PageRequest para facilitar o uso de paginação via
 * DWR.
 * 
 * @author eduardo
 *
 */
@DataTransferObject(javascript = "SimplePageRequest")
public class SimplePageRequest implements Serializable
{
	@DataTransferObject(type = "enum")
	public enum Direction
	{
		ASC, DESC;
	}

	private static final long serialVersionUID = -4541509938956089562L;

	private int page;
	private int size;
	private String property;
	private Direction direction;

	public int getPage()
	{
		return page;
	}

	public void setPage(int page)
	{
		this.page = page;
	}

	public int getSize()
	{
		return size;
	}

	public void setSize(int size)
	{
		this.size = size;
	}

	public String getProperty()
	{
		return property;
	}

	public void setProperty(String property)
	{
		this.property = property;
	}

	public Direction getDirection()
	{
		return direction;
	}

	public void setDirection(Direction direction)
	{
		this.direction = direction;
	}
	
	public PageRequest toPageRequest()
	{
		Sort sort;
		if(this.direction != null && this.property != null)
		{
			sort = new Sort(new Order(this.direction == SimplePageRequest.Direction.ASC ? Sort.Direction.ASC : Sort.Direction.DESC, this.property));
		}
		else
		{
			sort = null;
		}
		return new PageRequest(this.page, this.size, sort);
	}
}

package gov.nist.toolkit.repository.simple;

import gov.nist.toolkit.repository.api.RepositoryException;
import gov.nist.toolkit.repository.api.Type;

/**
 * Types are not be created dynamically, through this class.  Instead
 * they are loaded from configuration files and this constructor is
 * used by the loader to create the in-memory instance.
 * @author bmajur
 *
 */
public class SimpleType extends Type {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5856958238533928287L;

	public SimpleType(String keyword, String description) throws RepositoryException {
		super("", "", keyword, description);
		
		if (keyword == null || keyword.equals(""))
			throw new RepositoryException(RepositoryException.NULL_ARGUMENT + " : " +
					"Type keyword cannot be empty");
	}
	
	public SimpleType(String keyword) throws RepositoryException {
		super("", "", keyword, "");
		
		if (keyword == null || keyword.equals(""))
			throw new RepositoryException(RepositoryException.NULL_ARGUMENT + " : " +
					"Type keyword cannot be empty");
	}
	
	public String toString() {
		return getKeyword();
	}
}
/**
 * 
 */
package org.wikitolearn.wikirating.model.graph;

import java.util.Date;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.DateLong;

/**
 * @author aletundo
 *
 */
@NodeEntity( label = "History")
public class History {
	@DateLong
    private Date timestamp;
	@Relationship(type = "NEXT_CALCULATION", direction = Relationship.OUTGOING)
	private History nextCalculation;
}

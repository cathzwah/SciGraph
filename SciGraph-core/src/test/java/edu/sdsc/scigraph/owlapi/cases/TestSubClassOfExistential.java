package edu.sdsc.scigraph.owlapi.cases;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

import com.google.common.base.Optional;

import edu.sdsc.scigraph.frames.CommonProperties;
import edu.sdsc.scigraph.neo4j.GraphUtil;

public class TestSubClassOfExistential extends OwlTestCase {

  /**
   * See https://github.com/SciCrunch/SciGraph/wiki/MappingToOWL#subclassof-axioms
   * 
   * Reduction step should give us a simple edge {sub p super}
   */
  @Test
  public void testSubclass() {
    Node subclass = getNode("http://example.org/subclass");
    Node superclass = getNode("http://example.org/superclass");

    RelationshipType p = DynamicRelationshipType.withName("p");
    Relationship relationship = getOnlyElement(GraphUtil.getRelationships(subclass, superclass, p));
    assertThat("subclassOf relationship should start with the subclass.",
        relationship.getStartNode(), is(subclass));
    assertThat("subclassOf relationship should end with the superclass.",
        relationship.getEndNode(), is(superclass));
    assertThat("relationship has the correct uri",
        GraphUtil.getProperty(relationship, CommonProperties.URI, String.class),
        is(equalTo(Optional.of("http://example.org/p"))));
    assertThat("relationship is asserted",
        GraphUtil.getProperty(relationship, CommonProperties.ASSERTED, Boolean.class),
        is(equalTo(Optional.of(true))));
  }

}

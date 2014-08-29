package edu.sdsc.scigraph.owlapi.cases;

import static com.google.common.collect.Iterables.getOnlyElement;
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

public class TestExistentialClassAssertion extends OwlTestCase {

  @Test
  public void testSubclass() {
    Node i = getNode("http://example.org/i");
    Node c = getNode("http://example.org/c");
    RelationshipType p = DynamicRelationshipType.withName("p");

    Relationship r = getOnlyElement(GraphUtil.getRelationships(i, c, p, true));
    assertThat(GraphUtil.getProperty(r, CommonProperties.CONVENIENCE, Boolean.class),
        is(Optional.of(true)));
    assertThat(GraphUtil.getProperty(r, CommonProperties.OWL_TYPE, String.class),
        is(Optional.of("type")));
  }

}
/**
 * Copyright (C) 2014 The SciGraph authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import edu.sdsc.scigraph.owlapi.OwlRelationships;

public class TestEquivalentToIntersectionOf extends OwlTestCase {

  @Test
  public void testEquivalentToIntersectionOf() {
    Node anonymousClass = getNode("_:-574176990");
    Node fillerClass = getNode("http://example.org/fillerClass");

    RelationshipType p = DynamicRelationshipType.withName("p");
    Relationship r = getOnlyElement(GraphUtil.getRelationships(anonymousClass, fillerClass, p));

    assertThat(GraphUtil.getProperty(r, CommonProperties.CONVENIENCE, Boolean.class),
        is(Optional.of(true)));
    assertThat(GraphUtil.getProperty(r, CommonProperties.OWL_TYPE, String.class),
        is(Optional.of(OwlRelationships.OPERAND.name())));
  }

}

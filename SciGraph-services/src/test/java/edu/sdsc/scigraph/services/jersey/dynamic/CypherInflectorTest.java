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
package edu.sdsc.scigraph.services.jersey.dynamic;

import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.UriInfo;

import org.hamcrest.collection.IsIterableWithSize;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Transaction;

import com.google.common.base.Optional;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;

import edu.sdsc.scigraph.internal.CypherUtil;
import edu.sdsc.scigraph.internal.GraphAspect;
import edu.sdsc.scigraph.owlapi.OwlRelationships;
import edu.sdsc.scigraph.owlapi.curies.CurieUtil;
import edu.sdsc.scigraph.services.swagger.beans.resource.Apis;
import edu.sdsc.scigraph.util.GraphTestBase;

public class CypherInflectorTest extends GraphTestBase {

  Apis config = new Apis();
  ContainerRequestContext context = mock(ContainerRequestContext.class);
  UriInfo uriInfo = mock(UriInfo.class);
  Transaction tx = mock(Transaction.class);
  CurieUtil curieUtil = mock(CurieUtil.class);
  CypherInflector inflector;

  @Before
  public void setup() {
    CypherUtil cypherUtil = new CypherUtil(graphDb, curieUtil);
    addRelationship("http://x.org/#foo", "http://x.org/#fizz", OwlRelationships.RDFS_SUB_PROPERTY_OF);
    addRelationship("http://x.org/#bar", "http://x.org/#baz", OwlRelationships.RDFS_SUB_PROPERTY_OF);
    addRelationship("http://x.org/#1", "http://x.org/#2", DynamicRelationshipType.withName("fizz"));
    when(context.getUriInfo()).thenReturn(uriInfo);
    MultivaluedHashMap<String, String> map = new MultivaluedHashMap<>();
    map.put("rel_id", newArrayList("fizz"));
    when(uriInfo.getQueryParameters()).thenReturn(map);
    map = new MultivaluedHashMap<>();
    map.put("pathParam", newArrayList("pathValue"));
    when(uriInfo.getPathParameters()).thenReturn(map);
    when(curieUtil.getIri(anyString())).thenReturn(Optional.<String>absent());
    when(curieUtil.getCurie(anyString())).thenReturn(Optional.<String>absent());
    when(curieUtil.getIri("X:foo")).thenReturn(Optional.of("http://x.org/#foo"));
    inflector = new CypherInflector(graphDb, cypherUtil, curieUtil, config, new HashMap<String, GraphAspect>());
  }

  @Test
  public void inflectorAppliesCorrectly() {
    config.setQuery("MATCH (n) RETURN n");
    TinkerGraph graph = (TinkerGraph) inflector.apply(context).getEntity();
    assertThat(graph.getVertices(), IsIterableWithSize.<Vertex>iterableWithSize(6));
  }

  @Test
  public void inflectorAppliesCorrectly_withRelationshipEntailment() {
    config.setQuery("MATCH (n)-[r:foo!]-(m) RETURN n, r, m");
    TinkerGraph graph = (TinkerGraph) inflector.apply(context).getEntity();
    assertThat(getOnlyElement(graph.getEdges()).getLabel(), is("fizz"));
  }

  @Test
  public void inflectorAppliesCorrectly_withVariableRelationship() {
    config.setQuery("MATCH (n)-[r:${rel_id}]-(m) RETURN n, r, m");
    TinkerGraph graph = (TinkerGraph) inflector.apply(context).getEntity();
    assertThat(getOnlyElement(graph.getEdges()).getLabel(), is("fizz"));
  }

  @Test
  public void pathsAreReturnedCorrectly() {
    config.setQuery("MATCH (n {fragment:'foo'})-[path:subPropertyOf*]-(m) RETURN n, path, m");
    TinkerGraph graph = (TinkerGraph) inflector.apply(context).getEntity();
    assertThat(graph.getEdges(), IsIterableWithSize.<Edge>iterableWithSize(1));
  }

}

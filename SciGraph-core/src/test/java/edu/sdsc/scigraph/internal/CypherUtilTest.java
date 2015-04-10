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
package edu.sdsc.scigraph.internal;

import static com.google.common.collect.Sets.newHashSet;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

import java.util.Set;

import org.hamcrest.collection.IsMapContaining;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Result;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import edu.sdsc.scigraph.owlapi.OwlRelationships;
import edu.sdsc.scigraph.util.GraphTestBase;

public class CypherUtilTest extends GraphTestBase{

  CypherUtil util;

  @Before
  public void test() {
    util = new CypherUtil(graphDb);
    addRelationship("http://x.org/#foo", "http://x.org/#fizz", OwlRelationships.RDFS_SUB_PROPERTY_OF);
    addRelationship("http://x.org/#bar", "http://x.org/#baz", OwlRelationships.RDFS_SUB_PROPERTY_OF);
    addRelationship("http://x.org/#1", "http://x.org/#2", DynamicRelationshipType.withName("fizz"));
  }

  @Test
  public void substituteSingleRelationship() {
    Multimap<String, Object> valueMap = HashMultimap.create();
    valueMap.put("node_id", "HP_123");
    valueMap.put("rel_id", "RO_1");
    String actual = CypherUtil.substituteRelationships("({node_id}-[:${rel_id}!]-(end)", valueMap);
    assertThat(actual, is("({node_id}-[:RO_1!]-(end)"));
  }

  @Test
  public void substituteMultipleRelationships() {
    Multimap<String, Object> valueMap = HashMultimap.create();
    valueMap.put("node_id", "HP_123");
    valueMap.put("rel_id", "RO_1");
    valueMap.put("rel_id", "RO_2");
    String actual = CypherUtil.substituteRelationships("({node_id}-[:${rel_id}!]-(end)", valueMap);
    assertThat(actual, is("({node_id}-[:RO_2|RO_1!]-(end)"));
  }

  @Test
  public void entailmentRegex() {
    String result = util.entailRelationships("MATCH (n)-[:foo!]-(n2) RETURN n");
    assertThat(result, is("MATCH (n)-[:foo|fizz]-(n2) RETURN n"));
  }

  @Test
  public void multipleEntailmentRegex() {
    Set<String> types = util.getEntailedRelationshipTypes(newHashSet("foo", "bar"));
    assertThat(types, containsInAnyOrder("foo", "bar", "fizz", "baz"));
  }

  @Test
  public void flattenMap() {
    Multimap<String, Object> multiMap = HashMultimap.create();
    multiMap.put("foo", "bar");
    multiMap.put("foo", "baz");
    assertThat(CypherUtil.flattenMap(multiMap), IsMapContaining.<String, Object>hasEntry("foo", "baz"));
  }

  @Test
  public void executeExecutes() {
    Multimap<String, Object> params = HashMultimap.create();
    params.put("fragment", "1");
    params.put("rel", "fizz");
    Result result = util.execute("MATCH (n)<-[:${rel}!]-(n2) WHERE n.fragment = {fragment} RETURN n", params);
    assertThat(result.next().size(), is(1));
  }

}
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
package edu.sdsc.scigraph.owlapi.curies;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Optional;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;

import edu.sdsc.scigraph.frames.CommonProperties;
import edu.sdsc.scigraph.owlapi.curies.CurieUtil;
import edu.sdsc.scigraph.owlapi.curies.CurrieAdder;

public class CurieAdderTest {

  CurieUtil util = mock(CurieUtil.class);
  Graph graph = new TinkerGraph();
  CurrieAdder adder = new CurrieAdder();

  @Before
  public void setup() {
    when(util.getCurie(anyString())).thenReturn(Optional.<String>absent());
    when(util.getCurie("http://x.org/foo")).thenReturn(Optional.of("x:foo"));
    adder.curieUtil = util;
  }

  @Test
  public void curriesAreAdded() {
    Vertex v = graph.addVertex(null);
    v.setProperty(CommonProperties.URI, "http://x.org/foo");
    adder.addCuries(graph);
    assertThat((String)v.getProperty(CommonProperties.CURIE), is("x:foo"));
  }

  @Test
  public void nonFoundCuriesAreIgnored() {
    Vertex v = graph.addVertex(null);
    adder.addCuries(graph);
    assertThat(v.getPropertyKeys(), not(contains(CommonProperties.CURIE)));
  }

}

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
package edu.sdsc.scigraph.neo4j;

import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;

import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserters;

import edu.sdsc.scigraph.frames.CommonProperties;

public class GraphBatchImplTest extends GraphTestBase<GraphBatchImpl> {

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();
  
  @Override
  protected GraphBatchImpl createInstance() throws IOException {
    String path = folder.newFolder().getAbsolutePath();
    BatchInserter inserter = BatchInserters.inserter(path);
    return new GraphBatchImpl(inserter, CommonProperties.URI, newHashSet("prop1", "prop2"),
            newHashSet("prop1"), new IdMap(), new RelationshipMap());
  }

}

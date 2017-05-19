// Copyright 2017, Emmanouil Antonios Platanios. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License"); you may not
// use this file except in compliance with the License. You may obtain a copy of
// the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
// WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
// License for the specific language governing permissions and limitations under
// the License.

package org.platanios.tensorflow.api.core.client

import org.platanios.tensorflow.api._
import org.platanios.tensorflow.api.core.Graph
import org.platanios.tensorflow.api.ops.{Basic, Op}
import org.platanios.tensorflow.api.tensors.Tensor
import org.platanios.tensorflow.api.types.FLOAT32

import org.scalatest.junit.JUnitSuite
import org.junit.Test

/**
  * @author Emmanouil Antonios Platanios
  */
class FeedableSuite extends JUnitSuite {
  case class DummyFeedable() extends Feedable[Tensor] {
    val feed: Op.Output = Basic.placeholder(FLOAT32)

    override def toFeedMap(value: Tensor): Map[Op.Output, Tensor] = Map(feed -> value)
  }

  case class DummyFeedable2() extends Feedable[(Tensor, Tensor)] {
    val feed1: Op.Output = Basic.placeholder(FLOAT32)
    val feed2: Op.Output = Basic.placeholder(FLOAT32)

    override def toFeedMap(value: (Tensor, Tensor)): Map[Op.Output, Tensor] = {
      Map(feed1 -> value._1, feed2 -> value._2)
    }
  }

  @Test def testFeedable(): Unit = using(Graph()) { graph =>
    Op.createWith(graph) {
      val feedable1 = DummyFeedable()
      val feedable2 = DummyFeedable2()
      val feedable1FeedMap = feedable1.toFeedMap(Tensor(0))
      val feedable2FeedMap = feedable2.toFeedMap(Tensor(1), Tensor(2))
      assert(feedable1FeedMap === Map(feedable1.feed -> Tensor(0)))
      assert(feedable2FeedMap === Map(feedable2.feed1 -> Tensor(1), feedable2.feed2 -> Tensor(2)))
    }
  }
}
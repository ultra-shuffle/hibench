/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intel.hibench.common.streaming.metrics

import java.time.Duration
import java.util.{Collections, Properties}

import org.apache.kafka.clients.consumer.{KafkaConsumer => KafkaJavaConsumer}
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.ByteArrayDeserializer

import scala.collection.JavaConverters._

class KafkaConsumer(bootstrapServers: String, topic: String, partition: Int) {

  private val CLIENT_ID = "metrics_reader"
  private val pollTimeout = Duration.ofMillis(500)

  private val props = new Properties()
  props.put("bootstrap.servers", bootstrapServers)
  props.put("group.id", s"${CLIENT_ID}_${topic}_$partition")
  props.put("client.id", s"${CLIENT_ID}_${topic}_$partition")
  props.put("enable.auto.commit", "false")
  props.put("auto.offset.reset", "earliest")
  props.put("key.deserializer", classOf[ByteArrayDeserializer].getName)
  props.put("value.deserializer", classOf[ByteArrayDeserializer].getName)

  private val consumer = new KafkaJavaConsumer[Array[Byte], Array[Byte]](props)
  private val topicPartition = new TopicPartition(topic, partition)
  consumer.assign(Collections.singletonList(topicPartition))
  consumer.seekToBeginning(Collections.singletonList(topicPartition))

  private var exhausted = false
  private var buffer: Iterator[Array[Byte]] = Iterator.empty

  def next(): Array[Byte] = {
    if (!hasNext) throw new NoSuchElementException("No more messages")
    buffer.next()
  }

  def hasNext: Boolean = {
    if (buffer.hasNext) return true
    if (exhausted) return false

    pollOnce()
    if (buffer.hasNext) return true

    pollOnce()
    exhausted = true
    buffer.hasNext
  }

  def close(): Unit = {
    consumer.close()
  }

  private def pollOnce(): Unit = {
    val records = consumer.poll(pollTimeout)
    buffer = records.records(topicPartition).asScala.iterator.map(_.value())
  }
}

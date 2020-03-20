



# 第一章 Flink简介

## 1.1  **初识**Flink

```markdown
   Apache Flink是一个**框架和分布式处理引擎，用于对无界和有界数据流进行有状态计算**。Flink被设计在所有常见的集群环境中运行，以内存执行速度和任意规模来执行计算。
```

![1584352837473](F:\Typora\笔记\image\图片1.png)

```markdown
   Flink起源于Stratosphere项目，Stratosphere是在2010~2014年由3所地处柏林的大学和欧洲的一些其他的大学共同进行的研究项目，2014年4月Stratosphere的代码被复制并捐赠给了Apache软件基金会，参加这个孵化项目的初始成员是Stratosphere系统的核心开发人员，2014年12月，Flink一跃成为Apache软件基金会的顶级项目。
```

![1584352874722](F:\Typora\笔记\image\图片2.png)

```markdown
   在德语中，Flink一词表示快速和灵巧，项目采用一只松鼠的彩色图案作为logo，这不仅是因为松鼠具有快速和灵巧的特点，还因为柏林的松鼠有一种迷人的红棕色，而Flink的松鼠logo拥有可爱的尾巴，尾巴的颜色与Apache软件基金会的logo颜色相呼应，也就是说，这是一只Apache风格的松鼠。
```

![1584352902436](F:\Typora\笔记\image\图片3.png)

![1584352916211](F:\Typora\笔记\image\图片4.png)

```markdown
   Flink虽然诞生的早(2010年)，但是其实是起大早赶晚集，直到2015年才开始突然爆发热度。 

   在Flink被apache提升为顶级项目之后，阿里实时计算团队决定在阿里内部建立一个 Flink 分支 Blink，并对 Flink 进行大量的修改和完善，让其适应阿里巴巴这种超大规模的业务场景。

   **Blink**由2016年上线，服务于阿里集团内部搜索、推荐、广告和蚂蚁等大量核心实时业务。与2019年1月Blink正式开源，目前阿里70%的技术部门都有使用该版本。

   Blink比起Flink的优势就是对**SQL语法的更完善的支持以及执行SQL的性能提升**。
```

![1584352974610](F:\Typora\笔记\image\图片5.png)

### 1.1.2 选择Flink的理由

```markdown
   在流处理技术中，我们常见的**Storm**，曾风靡一时。他是流处理的先锋，Storm提供了低延时的流处理，但是他为了实时性付出了一些代价：**很难实现高吞吐，并且其正确性没能达到通常所需的水平**，也就是说不能保证 **exactly-once**语义。

   在低延时和高吞吐的流处理系统中维护良好的容错性是非常困难的，因此，开发人员提出将连续事件中的数据分割成一系列微小的批量作业。即便分割在小，也无法做到完全的实时，这个就是Spark Streaming的使用方法。使用微批处理方法，可以实现exactly-once语义，从而保障状态的一致性。如果一个微批处理作业失败了，它可以重新运行。这比连续的流处理方法更容易。

   **Storm Trident**是对Storm的延伸，它的底层流处理引擎就是基于微批处理方法来进行计算的，从而实现了exactly-once语义，但是在延迟性方面付出了很大的代价。

 通过间歇性的批处理作业来模拟流处理，会导致开发和运维相互交错。完成间歇性的批处理作业所需的时间和数据到达的时间紧密耦合，任何延迟都可能导致不一致(或者说错误)的结果。这种技术的潜在问题是，时间由系统中生成小批量作业的那一部分全权控制。Spark Streaming等一些流处理框架在一定程度上弱化了这一弊端，但还是不能完全避免。另外，使用这种方法的计算有着糟糕的用户体验，尤其是那些对延迟比较敏感的作业，而且人们需要在写业务代码时花费大量精力来提升性能。
   也因此，Flink诞生了。Flink的一个优势是，它拥有诸多重要的流式计算功能。其他项目为了实现这些功能，都不得不付出代价。

  比如，Storm实现了低延迟，但是做不到高吞吐，也不能在故障发生时准确地处理计算状态; 

  Spark Streaming通过采用微批处理方法实现了高吞吐和容错性，但是牺牲了低延迟和实时处理能力，也不能使窗口与自然时间相匹配，并且表现力欠佳。
```

看下面一张图，就是Flink , Spark Streaming , Storm三者之间的区别。

![](F:\Typora\笔记\image\20190829095414290.png)

### 1.1.3 Spark Streaming VS Flink

**Micro Batching 模式（Spark）**
        该计算模式认为**流是批的特例**，流计算就是连续不断的批进行持续计算，但是该计算模式在一定程度上可以满足99%的实时计算场景，在该模式的架构实现上有一个自然流数据进入系统进行攒批的过程，这样就增加了延迟。如图所示：

![](F:\Typora\笔记\image\20190829095749628.png)

        可以看出，把输入流分割成微小的批次，然后一个批次一个批次的处理，一个批次一个批次的输出。

**Native Streaming 模式（Flink）**

```tx
    该计算模式认为批是流的特例，其是将每条数据都进行计算，这种计算模式很自然，并且延迟性能更低，如图所示：
```

![](F:\Typora\笔记\image\20190829095852751.png)

### 1.1.4 数据模型

```markdown
  **Spark **  最早使用的是RDD模型，这个比MapReduce快了100倍的计算模型有着显著的优势。对Hadoop生态大幅升级换代。**RDD弹性数据集是分割为固定大小的批数据**。
  **Spark Streaming**  里的DStream和RDD模型类似，把一个实时进来的无线数据分割为一个小批数据集合DStream，定时器定时通知系统去处理这些微批数据。然而，API少，无法胜任复杂的流计算业务，调大吞吐量而不触发背压是个体力活，不支持乱序处理。

   **Flink**  的基本数据模型是数据流，及事件（Event）的序列。数据流作为数据的基本模型可能没有表或者数据块的直观熟悉，但是可以证明是完全等效的。流可以是无边界的无限流，即所谓的流处理。可以说，有边界的有限流，这样就是批处理。
   Flink采用**Dataflow模型**，和Lambda模式不同。

   Dataflow是纯粹的节点组成的一个图，图中的节点可以执行流计算、批计算、机器学习算法，流数据在节点间流动，被节点上的处理函数实时apply处理，节点间使用Netty连接起来的。两个Netty间Keepalive，网络Buffer是自然反压的关键。经过逻辑优化和物理优化，Dadaflow的逻辑关系和运行的物理拓扑相差不大。这种纯粹的流式设计，时延和吞吐理论山是最优的。
```

![](F:\Typora\笔记\image\20190829095539191.png)

### 1.1.5运行时架构

**Spark运行时架构**
        批计算是把DAG划分为不同stage，DAG节点之间有血缘关系，在运行期间一个stage的task任务列表执行完毕，销毁再去执行下一个stage；

```tx
  Spark Streaming则是对持续流入的数据划分一个批次，定时去执行批次的数据运算。

  Structured Streaming将无限输入流保存在状态存储中，对流数据做微批或实时的计算，跟Dataflow模型比较像。
```
**Flink运行时架构**
       Flink有统一的runtime，在此之上可以是Batch API、Stream API、ML、Graph、CEP等，DAG中的节点上执行上述模块的功能函数，DAG会一步步转化成ExecutionGraph，即物理可执行的图，最终交给调度系统。节点中的逻辑在资源池中的task上被apply执行，task和Spark中的task类似，都对应线程池中的一个线程。

在DAG的执行上，Spark和Flink有一个比较显著的区别。在Flink的流执行模式中，一个事件在一个节点处理完后的输出就可以发到下一个节点立即处理。这样执行引擎并不会引入额外的延迟。与之相应的，所有节点是需要同时运行的。而Spark的micro batch和一般的batch执行一样，处理完上游的stage得到输出之后才开始下游的stage。

在流计算的运行时架构方面，Flink明显更为统一且优雅一些。

[原文链接]: https://blog.csdn.net/qq_33356083/article/details/100131869	"flink简介"

## 1.2 Flink的重要特点 

### 1.2.1 事件驱动型(Event-driven)

```markdown
   事件驱动型应用是一类具有状态的应用，它从一个或多个事件流提取数据，并根据到来的事件触发计算、状态更新或其他外部动作。比较典型的就是以kafka为代表的消息队列几乎都是事件驱动型应用。

 与之不同的就是SparkStreaming微批次，如图：
```

![](F:\Typora\笔记\image\图片6.png)

   事件驱动型：

![](F:\Typora\笔记\image\图片7.png)

### 1.2.2 流与批的世界观

```markdown
   **批处理**的特点是有界、持久、大量，非常适合需要访问全套记录才能完成的计算工作，一般用于离线统计。

   **流处理**的特点是无界、实时,  无需针对整个数据集执行操作，而是对通过系统传输的每个数据项执行操作，一般用于实时统计。

   **在spark的世界观中**，一切都是由批次组成的，离线数据是一个大批次，而实时数据是由一个一个无限的小批次组成的。

   **而在flink的世界观中**，一切都是由流组成的，离线数据是有界限的流，实时数据是一个没有界限的流，这就是所谓的有界流和无界流。

   **无界数据流：无界数据流有一个开始但是没有结束**，它们不会在生成时终止并提供数据，必须连续处理无界流，也就是说必须在获取后立即处理event。对于无界数据流我们无法等待所有数据都到达，因为输入是无界的，并且在任何时间点都不会完成。处理无界数据通常要求以特定顺序（例如事件发生的顺序）获取event，以便能够推断结果完整性。
   **有界数据流：有界数据流有明确定义的开始和结束**，可以在执行任何计算之前通过获取所有数据来处理有界流，处理有界流不需要有序获取，因为可以始终对有界数据集进行排序，有界流的处理也称为批处理。
```

![](F:\Typora\笔记\image\图片8.png)



     **这种以流为世界观的架构，获得的最大好处就是具有极低的延迟。**

### 1.2.3 分层API

![](F:\Typora\笔记\image\图片9.png)

```markdown
   最底层级的抽象仅仅提供了有状态流，它将通过**过程函数（Process Function）**被嵌入到DataStream API中。底层过程函数（Process Function） 与 DataStream API 相集成，使其可以对某些特定的操作进行底层的抽象，它允许用户可以自由地处理来自一个或多个数据流的事件，并使用一致的容错的状态。除此之外，用户可以注册事件时间并处理时间回调，从而使程序可以处理复杂的计算。

   实际上，**大多数应用并不需要上述的底层抽象，而是针对核心API（Core APIs） 进行编程，比如DataStream API（有界或无界流数据）以及DataSet API（有界数据集）。**这些API为数据处理提供了通用的构建模块，比如由用户定义的多种形式的转换（transformations），连接（joins），聚合（aggregations），窗口操作（windows）等等。DataSet API 为有界数据集提供了额外的支持，例如循环与迭代。这些API处理的数据类型以类（classes）的形式由各自的编程语言所表示。

   Table API 是以表为中心的声明式编程，其中表可能会动态变化（在表达流数据时）。Table API遵循（扩展的）关系模型：表有二维数据结构（schema）（类似于关系数据库中的表），同时API提供可比较的操作，例如select、project、join、group-by、aggregate等。Table API程序声明式地定义了什么逻辑操作应该执行，而不是准确地确定这些操作代码的看上去如何 。 尽管Table API可以通过多种类型的用户自定义函数（UDF）进行扩展，其仍不如核心API更具表达能力，但是使用起来却更加简洁（代码量更少）。除此之外，Table API程序在执行之前会经过内置优化器进行优化。
   **你可以在表与 DataStream/DataSet 之间无缝切换，以允许程序将 Table API 与 DataStream 以及 DataSet 混合使用。**
   Flink提供的最高层级的抽象是 SQL 。这一层抽象在语法与表达能力上与 Table API 类似，但是是以SQL查询表达式的形式表现程序。SQL抽象与Table API交互密切，同时SQL查询可以直接在Table API定义的表上执行。
```

### 1.2.4 支持有状态计算

```tx
    Flink在1.4版本中实现了状态管理，所谓状态管理就是在流失计算过程中将算子的中间结果保存在内存或者文件系统中，等下一个事件进入算子后可以让当前事件的值与历史值进行汇总累计。
```

#### 1.2.4.1有状态的流式处理简介

```tx
   Apache Flink是一个分布式流处理器，具有直观和富有表现力的API，可实现有状态的流处理应用程序。它以容错的方式有效地大规模运行这些应用程序。
```

##### 1.2.4.1.1传统数据处理架构

```tx
   两种数据处理类型：事务处理（OLTP）和分析处理（OLAP）
```

###### 1.2.4.1.1.1事务处理

```tx
   公司系统通常设计有单独的层，用于数据处理（应用程序本身）和数据存储（事务数据库系统）。应用程序通常连接到外部服务或直接面向用户，并持续处理传入的事件。处理事件时，应用程序会读取数据库的状态，或者通过运行事务来更新它。
```

###### 1.2.4.1.1.2分析处理

```tx
    一般不会直接在事务数据库上运行分析查询，而是复制数据到数据仓库。数据仓库是对工作负载进行分析和查询的专用数据存储。为了填充数据仓库，需要将事务数据库系统管理的数据复制过来。将数据复制到数据仓库的过程称为extract-transform-load（ETL）。 ETL过程从事务数据库中提取数据，将其转换为某种通用的结构表示，可能包括验证，值的规范化，编码，重复数据删除（去重）和模式转换，最后将其加载到分析数据库中。 ETL过程可能非常复杂，并且通常需要技术复杂的解决方案来满足性能要求。 ETL过程需要定期运行以保持数据仓库中的数据同步。

   将数据导入数据仓库后，可以查询和分析数据。通常，在数据仓库上执行两类查询。

   第一种类型是定期报告查询，用于计算与业务相关的统计信息，比如收入、用户增长或者输出的产量。这些指标汇总到报告中，帮助管理层评估业务的整体健康状况。

  第二种类型是即席查询，旨在提供特定问题的答案并支持关键业务决策，例如收集统计在投放商业广告上的花费，和获取的相应收入，以评估营销活动的有效性。

  两种查询由批处理方式由数据仓库执行
```

![](F:\Typora\笔记\image\图片10.jpg)

#### 1.2.4.1.2有状态的流式处理

```markdown
   如果我们想要无限处理事件流，并且不愿意繁琐地每收到一个事件就记录一次，那这样的应用程序就需要是有状态的，也就是说能够存储和访问中间数据。当应用程序收到一个新事件时，它可以从状态中读取数据，或者向该状态写入数据，总之可以执行任何计算。原则上讲，我们可以在各种不同的地方存储和访问状态，包括程序变量（内存）、本地文件，还有嵌入式或外部数据库。

   Apache Flink将应用程序状态，存储在内存或者嵌入式数据库中。由于Flink是一个分布式系统，因此需要保护本地状态以防止在应用程序或计算机故障时数据丢失。 Flink通过定期将应用程序状态的一致性检查点（check point）写入远程且持久的存储，来保证这一点。状态、状态一致性和Flink的检查点将在后面的章节中更详细地讨论，但是，现在图1-4显示了有状态的流式Flink应用程序。
```

![](F:\Typora\笔记\image\图片11.jpg)


```markdown
   有状态的流处理应用程序，通常从事件日志中提取输入事件。事件日志就用来存储和分发事件流。事件被写入持久的仅添加（append-only）日志，这意味着无法更改写入事件的顺序。写入事件日志的流，可以被相同或不同的消费者多次读取。由于日志的仅附加（append-only）属性，事件始终以完全相同的顺序发布给所有消费者。现在已有几种事件日志系统，其中Apache Kafka是最受欢迎的，可以作为开源软件使用，或者是云计算提供商提供的集成服务。

   在Flink上运行的有状态的流处理应用程序，是很有意思的一件事。在这个架构中，事件日志会按顺序保留输入事件，并且可以按确定的顺序重播它们。如果发生故障，Flink将从先前的检查点（check point）恢复其状态，并重置事件日志上的读取位置，这样就可以恢复整个应用。应用程序将重放（并快进）事件日志中的输入事件，直到它到达流的尾部。此技术一般用于从故障中恢复，但也可用于更新应用程序、修复bug或者修复以前发出的结果，另外还可以用于将应用程序迁移到其他群集，或使用不同的应用程序版本执行A / B测试。

   如前所述，有状态的流处理是一种通用且灵活的设计架构，可用于许多不同的场景。在下文中，我们提出了三类通常使用有状态流处理实现的应用程序：（1）事件驱动应用程序，（2）数据管道应用程序，以及（3）数据分析应用程序。

   我们将应用程序分类描述，是为了强调有状态流处理适用于多种业务场景；而实际的应用中，往往会具有以上多种情况的特征。
```

##### 1.2.4.2.1 事件驱动应用程序（Event-Driven Applications）

```tx
    事件驱动的应用程序是由状态的流应用程序，使用特定的业务逻辑来提取事件流并处理事件。
```

##### 1.2.4.2.2 数据管道（Data Pipelines）

```markdown
   以较低的延迟，来提取、转换和插入数据是有状态流处理应用程序的另一个常见应用场景。这种类型的应用程序称为数据管道（data pipeline）。数据管道必须能够在短时间内处理大量数据。操作数据管道的流处理器还应具有许多源（source）和接收器（sink）的连接器，以便从各种存储系统读取数据并将数据写入各种存储系统。当然，同样地，Flink完成了所有这些功能。
```

##### 1.2.4.2.3 流分析

```markdown
   ETL作业定期将数据导入数据存储区，数据的处理是由即席查询（用户自定义查询）或设定好的通常查询来做的。无论架构是基于数据仓库还是基于Hadoop生态系统的组件，这都是批处理。多年来最好的处理方式就是，定期将数据加载到数据分析系统中，但它给分析管道带了的延迟相当大，而且无法避免。

   流式分析应用程序不是等待定期触发，而是连续地提取事件流，并且通过纳入最新事件来更新其计算结果，这个过程是低延迟的。这有些类似于数据库中用于更新视图（views）的技术。通常，流应用程序将其结果存储在支持更新的外部数据存储中，例如数据库或键值（key-value）存储。流分析应用程序的实时更新结果可用于驱动监控仪表板（dashboard）应用程序。

   流分析应用程序最大的优势就是，将每个事件纳入到分析结果所需的时间短得多。

  除此之外，流分析应用程序还有另一个不太明显的优势。传统的分析管道由几个独立的组件组成，例如ETL过程、存储系统、对于基于Hadoop的环境，还包括用于触发任务（jobs）的数据处理和调度程序。相比之下，如果我们运行一个有状态流应用程序，那么流处理器就会负责所有这些处理步骤，包括事件提取、带有状态维护的连续计算以及更新结果。

  此外，流处理器可以从故障中恢复，并且具有精确一次（exactly-once）的状态一致性保证，还可以调整应用程序的计算资源。像Flink这样的流处理器还支持事件时间（event-time）处理，这可以保证产生正确和确定的结果，并且能够在很短的时间内处理大量数据。
```

### 1.2.5 支持exactly-once语义

```markdown
   在分布式系统中，组成系统的各个计算机是独立的。这些计算机有可能fail。
   一个sender发送一条message到receiver。根据receiver出现fail时sender如何处理fail，可以将message delivery分为三种语义:

  **At Most once**: 对于一条message,receiver最多收到一次(0次或1次).
  可以达成At Most Once的策略:
  sender把message发送给receiver.无论receiver是否收到message,sender都不再重发message.

   **At Least once**: 对于一条message,receiver最少收到一次(1次及以上)。
   可以达成At Least Once的策略:
   sender把message发送给receiver.当receiver在规定时间内没有回复ACK或回复了error信息,那么sender重发这条message给receiver,直到sender收到receiver的ACK.

  **Exactly once**: 对于一条message,receiver确保只收到一次。
```

### 1.2.6 支持事件时间（EventTime）

```markdown
   目前大多数框架时间窗口计算，都是采用当前系统时间，以时间为单位进行的聚合计算只能反应数据到达计算引擎的时间，而并不是实际业务时间。
```

## 1.3 Flink简介总结

Apache Flink是第三代分布式流处理器，它拥有极富竞争力的功能。它提供准确的大规模流处理，具有高吞吐量和低延迟。特别的是，以下功能使Flink脱颖而出：

- 事件时间（event-time）和处理时间（processing-tme）语义。即使对于无序事件流，事件时间（event-time）语义仍然能提供一致且准确的结果。而处理时间（processing-time）语义可用于具有极低延迟要求的应用程序。
- 精确一次（exactly-once）的状态一致性保证。
- 每秒处理数百万个事件，毫秒级延迟。 Flink应用程序可以扩展为在数千个核（cores）上运行。
- 分层API，具有不同的权衡表现力和易用性。本书介绍了DataStream API和过程函数（process function），为常见的流处理操作提供原语，如窗口和异步操作，以及精确控制状态和时间的接口。本书不讨论Flink的关系API，SQL和LINQ风格的Table API。
- 连接到最常用的存储系统，如Apache Kafka，Apache Cassandra，Elasticsearch，JDBC，Kinesis和（分布式）文件系统，如HDFS和S3。
- 由于其高可用的设置（无单点故障），以及与Kubernetes，YARN和Apache Mesos的紧密集成，再加上从故障中快速恢复和动态扩展任务的能力，Flink能够以极少的停机时间 7 * 24全天候运行流应用程序。
- 能够更新应用程序代码并将作业（jobs）迁移到不同的Flink集群，而不会丢失应用程序的状态。
- 详细且可自定义的系统和应用程序指标集合，以提前识别问题并对其做出反应。
- 最后但同样重要的是，Flink也是一个成熟的批处理器。



# 第二章 快速上手

##  1.搭建maven工程

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.2.5.RELEASE</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.example</groupId>
	<artifactId>demo</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>Flink</name>
	<description>Demo project for Spring Boot</description>


	<properties>
		<java.version>1.8</java.version>
		<compiler.version>1.8</compiler.version>
		<flink.version>1.9.0</flink.version>
		<java.version>1.8</java.version>
		<jackson.version>2.9.6</jackson.version>
		<scala.binary.version>2.11</scala.binary.version>
		<maven.compiler.source>1.8</maven.compiler.source>
		<maven.compiler.target>1.8</maven.compiler.target>
		<fastjson.version>1.2.61</fastjson.version>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
	</properties>

	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-devtools</artifactId>
			<scope>runtime</scope>
			<optional>true</optional>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
			<exclusions>
				<exclusion>
					<groupId>org.junit.vintage</groupId>
					<artifactId>junit-vintage-engine</artifactId>
				</exclusion>
			</exclusions>
		</dependency>


		<dependency>
			<groupId>com.alibaba</groupId>
			<artifactId>fastjson</artifactId>
			<version>${fastjson.version}</version>
		</dependency>
		
		<!-- Apache Flink dependencies -->
		<!-- These dependencies are provided, because they should not be packaged into the JAR file. -->
		<dependency>
			<groupId>org.apache.flink</groupId>
			<artifactId>flink-java</artifactId>
			<version>${flink.version}</version>
			<!-- <scope>provided</scope>-->
		</dependency>
		<dependency>
			<groupId>org.apache.flink</groupId>
			<artifactId>flink-streaming-java_${scala.binary.version}</artifactId>
			<version>${flink.version}</version>
			<!-- <scope>provided</scope>-->
		</dependency>
		<dependency>
			<groupId>org.apache.flink</groupId>
			<artifactId>flink-clients_2.12</artifactId>
			<version>${flink.version}</version>
		</dependency>
		<dependency>
			<groupId>org.apache.flink</groupId>
			<artifactId>flink-connector-kafka_2.11</artifactId>
			<version>1.9.0</version>
		</dependency>

		<!-- Add logging framework, to produce console output when running in the IDE. -->
		<!-- These dependencies are excluded from the application JAR by default. -->
		<dependency>
			<groupId>org.slf4j</groupId>
			<artifactId>slf4j-log4j12</artifactId>
			<version>1.7.7</version>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>log4j</groupId>
			<artifactId>log4j</artifactId>
			<version>1.2.17</version>
			<scope>runtime</scope>
		</dependency>

		<dependency>
			<groupId>junit</groupId>
			<artifactId>junit</artifactId>
			<version>3.8.1</version>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<version>1.18.2</version>
			<optional>true</optional>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-autoconfigure</artifactId>
			<version>2.1.1.RELEASE</version>
			<scope>compile</scope>
		</dependency>

	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-compiler-plugin</artifactId>
				<version>3.3</version>
				<configuration>
					<source>1.8</source>
					<target>1.8</target>
				</configuration>
			</plugin>
			<!--打jar包-->
			<plugin>
				<artifactId>maven-assembly-plugin</artifactId>
				<configuration>
					<archive>
						<manifest>
							<Main-Class>${app.main.class}</Main-Class>
						</manifest>
					</archive>
					<descriptorRefs>
						<descriptorRef>jar-with-dependencies</descriptorRef>
					</descriptorRefs>
				</configuration>
			</plugin>

			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-shade-plugin</artifactId>
				<version>2.3</version>
				<executions>
					<execution>
						<phase>package</phase>
						<goals>
							<goal>shade</goal>
						</goals>
						<configuration>
							<transformers>
								<transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
									<mainClass>${app.main.class}</mainClass>
								</transformer>
								<transformer implementation="org.apache.maven.plugins.shade.resource.AppendingTransformer">
									<resource>reference.conf</resource>
								</transformer>
							</transformers>
						</configuration>
					</execution>
				</executions>
			</plugin>
		</plugins>
	</build>

</project>
```

## 2.批处理wordcount

```java
package com.example.demo.test;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;


public class Test2 {
    public static void main(String[] args) throws Exception {
        //构造执行环境
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        //读取文本流
        DataSource<String> dataStreamSource = env.readTextFile("C:\\Users\\Administrator\\Desktop\\info.log");
        //数据聚合
        dataStreamSource.flatMap(new FlatMapFunction<String, Tuple2<String,Integer>>() {
            @Override
            public void flatMap(String value, Collector<Tuple2<String, Integer>> collector) throws Exception {
                String[] tokens = value.toLowerCase().split("\t");
                for (String token:tokens){
                    if(token.length()>0){
                        collector.collect(new Tuple2<String, Integer>(token,1));
                    }
                }
            }
            //经过groupby进行分组，sum进行聚合
        }).groupBy(0).sum(1).print();
    }
}

```

## 3.流处理 wordcount

```java
package com.example.demo.test;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

public class StreamingTest {
    public static void main(String[] args) throws Exception {
                //设置端口
//        int port = 0;
//        try {
//            //参数工具
//            ParameterTool parameterTool = ParameterTool.fromArgs(args);
//            port = parameterTool.getInt("port");
//        } catch (Exception e) {
//            System.out.println("端口未设置，使用默认端口9999");
//            port = 9999;
//        }
        //构造流处理环境
        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        //读取文件
        DataStreamSource<String> stringDataStreamSource = executionEnvironment.socketTextStream("localhost",9999);
        //数据处理
        stringDataStreamSource.flatMap(new FlatMapFunction<String,Tuple2<String,Integer>>() {
            @Override
            public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {
                String[] tokens = value.toLowerCase().split(",");
                for (String token:tokens){
                    if(token.length()>0){
                        out.collect(new Tuple2<String, Integer>(token,1));
                    }
                }
            }
        }).keyBy(0) //流式 分组
                .timeWindow(Time.seconds(5)) //每5秒执行输出一次
                .sum(1) //统计
                .print();
		//执行
        executionEnvironment.execute("Stream JOb");
    }
}
```

# 第三章 Flink运行架构

## 1.任务提交流程（yarn模式）

![](F:\Typora\笔记\image\图片12.png)

- Flink任务提交后，Client向**HDFS**上传Flink的Jar包和配置
- 向Yarn **ResourceManager**提交任务，ResourceManager分配**Container**资源并通知对应的**NodeManager**启动**ApplicationMaster**
- ApplicationMaster启动后加载Flink的Jar包和配置构建环境，然后启动**JobManager**
- ApplicationMaster向ResourceManager申请资源启动**TaskManager**，ResourceManager分配Container资源后
- ApplicationMaster通知资源所在节点的NodeManager启动TaskManager，NodeManager加载Flink的Jar包和配置构建环境并启动TaskManager，TaskManager启动后向JobManager发送心跳包，并等待JobManager向其分配任务。

## 2. 任务调度原理

![](F:\Typora\笔记\image\图片13.png)

```markdown
   客户端不是运行时和程序执行的一部分，但它用于准备并发送dataflow(JobGraph)给Master(JobManager)，然后，客户端断开连接或者维持连接以等待接收计算结果。

   当 Flink 集群启动后，首先会启动一个 JobManger 和一个或多个的 TaskManager。由 Client 提交任务给 JobManager，JobManager 再调度任务到各个 TaskManager 去执行，然后 TaskManager 将心跳和统计信息汇报给 JobManager。TaskManager 之间以流的形式进行数据的传输。上述三者均为独立的 JVM 进程。

  **Client** 为提交 Job 的客户端，可以是运行在任何机器上（与 JobManager 环境连通即可）。提交 Job 后，Client 可以结束进程（Streaming的任务），也可以不结束并等待结果返回。

  **JobManager** 主要负责调度 Job 并协调 Task 做 checkpoint，职责上很像 Storm 的 Nimbus。从 Client 处接收到 Job 和 JAR 包等资源后，会生成优化后的执行计划，并以 Task 的单元调度到各个 TaskManager 去执行。                 

  **TaskManager** 在启动的时候就设置好了槽位数（Slot），每个 slot 能启动一个 Task，Task 为线程。从 JobManager 处接收需要部署的 Task，部署启动后，与自己的上游建立 Netty 连接，接收数据并处理。
```

## 3.Worker与Slots 

```markdown
   **每一个worker(TaskManager)是一个JVM进程，它可能会在独立的线程上执行一个或多个subtask**。为了控制一个worker能接收多少个task，worker通过task slot来进行控制（一个worker至少有一个task slot）。
   
   每个task slot表示TaskManager拥有资源的一个固定大小的子集。假如一个TaskManager有三个slot，那么它会将其管理的内存分成三份给各个slot。**资源slot化意味着一个subtask将不需要跟来自其他job的subtask竞争被管理的内存，取而代之的是它将拥有一定数量的内存储备**。需要注意的是，这里不会涉及到CPU的隔离，slot目前仅仅用来隔离task的受管理的内存。

  **通过调整task slot的数量，允许用户定义subtask之间如何互相隔离**。如果一个TaskManager一个slot，那将意味着每个task group运行在独立的JVM中（该JVM可能是通过一个特定的容器启动的），而一个TaskManager多个slot意味着更多的subtask可以共享同一个JVM。而在同一个JVM进程中的task将共享TCP连接（基于多路复用）和心跳消息。它们也可能共享数据集和数据结构，因此这减少了每个task的负载。
```

![](F:\Typora\笔记\image\图片14.png)

```markdown
   **TasKSlot是静态的概念，是指TaskManager具有的并发执行能力**，可以通过参数taskmanager.numberOfTaskSlots进行配置，而**并行度parallelism是动态概念，即TaskManager运行程序时实际使用的并发能力**，可以通过参数parallelism.default进行配置。

   也就是说，假设一共有3个TaskManager，每一个TaskManager中的分配3个TaskSlot，也就是每个TaskManager可以接收3个task，一共9个TaskSlot，如果我们设置parallelism.default=1，即运行程序默认的并行度为1，9个TaskSlot只用了1个，有8个空闲，因此，设置合适的并行度才能提高效率。
```

![](F:\Typora\笔记\image\图片15.png)

![](F:\Typora\笔记\image\图片16.png)

## 4.并行数据流

```markdown
   **Flink程序的执行具有并行、分布式的特性**。在执行过程中，一个 stream 包含一个或多个 stream partition ，而每一个 operator 包含一个或多个 operator subtask，这些operator subtasks在不同的线程、不同的物理机或不同的容器中彼此互不依赖得执行。
   **一个特定operator的subtask的个数被称之为其parallelism(并行度)**。一个stream的并行度总是等同于其producing operator的并行度。一个程序中，不同的operator可能具有不同的并行度。
```

![](F:\Typora\笔记\image\图片17.png)

```markdown
   Stream在operator之间传输数据的形式可以是one-to-one(forwarding)的模式也可以是redistributing的模式，具体是哪一种形式，取决于operator的种类。

   **One-to-one**：stream(比如在source和map operator之间)维护着分区以及元素的顺序。

   那意味着map operator的subtask看到的元素的个数以及顺序跟source operator的subtask生产的元素的个数、顺序相同，map、fliter、flatMap等算子都是one-to-one的对应关系。
```

> 类似于spark中的窄依赖

```markdown
   **Redistributing**：stream(map()跟keyBy/window之间或者keyBy/window跟sink之间)的分区会发生改变。每一个operator subtask依据所选择的transformation发送数据到不同的目标subtask。例如，keyBy() 基于hashCode重分区、broadcast和rebalance会随机重新分区，这些算子都会引起redistribute过程，而redistribute过程就类似于Spark中的shuffle过程。
```

> 类似于spark中的宽依赖

## 5.task与operator chains

```markdown
   相同并行度的one to one操作，Flink这样相连的operator 链接在一起形成一个task，原来的operator成为里面的subtask。将operators链接成task是非常有效的优化：**它能减少线程之间的切换和基于缓存区的数据交换，在减少时延的同时提升吞吐量**。链接的行为可以在编程API中进行指定。
```

![](F:\Typora\笔记\image\图片18.png)

OperatorChain的优点

- 减少线程切换

- 减少序列化与反序列化

- 减少延迟并且提高吞吐能力

  OperatorChain 组成条件（重要）

- 上下游算子并行度一致
- 上下游算子之间没有数据shuffle

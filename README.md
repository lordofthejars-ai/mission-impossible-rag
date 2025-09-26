# mission-impossible-rag

This project shows the integration between Quarkus and Docling (https://docling-project.github.io/docling/) for implementing RAG.

Moreover, you'll see best practices about RAG like anonymizing, signature verification, or preserve distance encryption for vectors.

This project is an AI application using:

* Quarkus
* LangChain4J (RAG + Tools) + ironcore-alloy + Bouncy Castle
* Redis
* H2

To start the example you need to:

Set QUARKUS_LANGCHAIN4J_OPENAI_API_KEY with your OpenAI API Key.

Have Docker or Podman Desktop installed on your computer in order for Quarkus to automatically start Redis and MongoDB.

Run ./mvnw quarkus:dev

Check the video for a demo and the multiple options you can enable.

Video: https://youtu.be/t1yU4gxL1ao

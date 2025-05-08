<template>
    <div class="about">
      <h1>This is the chat space for chat  {{ chatId }}</h1>
    </div>
</template>


<script>
export default {
    name: "ChatingView",
    components: {

    },
    props: {
        chatId: {type: String, required: true},
    }
}

</script>
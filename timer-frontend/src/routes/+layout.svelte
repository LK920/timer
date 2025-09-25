<script>
  import '../app.css';
</script>

<main>
  <slot />
</main>

<style>
  main {
    width: 100%;
    min-height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 15px;
    box-sizing: border-box;
  }
</style>
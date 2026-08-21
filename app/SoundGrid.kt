@Composable
fun SoundGrid(
    items: List<SoundFolder>
) {
    val context = LocalContext.current

    val fullItems = remember(items) {
        listOf(
            SoundFolder(
                id = 0,
                name = context.getString(R.string.add_custom),
                group = "custom",
                thumb = "file:///android_asset/ic_sound_error.png"
            )
        ) + items
    }

    var selectedIndex by remember { mutableIntStateOf(-1) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(fullItems) { index, folder ->
            ItemSound(
                data = folder,
                isSelected = selectedIndex == index,
                onClick = {
                    selectedIndex = if (selectedIndex == index) -1 else index
                }
            )
        }
    }
}
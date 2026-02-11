package com.itd.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itd.app.data.model.Clan
import com.itd.app.ui.theme.*

@Composable
fun TopClansSection(clans: List<Clan>, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        Text(
            text = "Топ кланов",
            style = MaterialTheme.typography.titleMedium,
            color = ITDOnSurface,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            itemsIndexed(clans) { index, clan ->
                ClanChip(rank = index + 1, clan = clan)
            }
        }
    }
}

@Composable
fun ClanChip(rank: Int, clan: Clan) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = ITDChip
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = rank.toString(),
                style = MaterialTheme.typography.labelMedium,
                color = ITDOnSurfaceVariant
            )
            Text(
                text = clan.avatar,
                fontSize = 18.sp
            )
            Text(
                text = formatCount(clan.memberCount),
                style = MaterialTheme.typography.labelMedium,
                color = ITDOnSurfaceVariant
            )
        }
    }
}

package com.youme.naya.screens.schedule

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.youme.naya.R
import com.youme.naya.components.PrimaryBigButton
import com.youme.naya.schedule.ScheduleMainViewModel
import com.youme.naya.ui.theme.*
import com.youme.naya.widgets.calendar.AnimatedCalendar
import com.youme.naya.widgets.common.HeaderBar
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import androidx.navigation.NavController
import com.youme.naya.components.RegisterButton
import com.youme.naya.database.entity.Member
import com.youme.naya.database.entity.Member.Companion.memberIcons
import com.youme.naya.schedule.component.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScheduleCreateScreen(
    navController: NavController,
    viewModel: ScheduleMainViewModel = hiltViewModel(),
) {
    val componentVariable = remember {
        mutableStateOf(0)
    }

    val memberType = remember {
        mutableStateOf(-1)
    }

    val memberNum = remember {
        mutableStateOf(0)
    }

    Column {
        // 상단 바
        when (componentVariable.value) {
            0 -> HeaderBar(navController = navController, title = "일정 등록")
            else -> {
                TopAppBar(
                    modifier = Modifier.height(64.dp),
                    backgroundColor = NeutralWhite,
                    elevation = 0.dp,
                    contentPadding = PaddingValues(horizontal = 8.dp),
                ) {
                    Row(
                        Modifier.height(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            componentVariable.value = componentVariable.value - 1
                        }) {
                            Image(
                                painter = painterResource(R.drawable.ic_baseline_arrow_back_ios_24),
                                contentDescription = "Prev page button",
                                colorFilter = ColorFilter.tint(NeutralLight)
                            )
                        }
                        Text(
                            "일정 등록",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 48.dp),
                            textAlign = TextAlign.Center,
                            fontFamily = fonts,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = PrimaryDark
                        )
                    }
                }
            }
        }

        // 캘린더
        AnimatedCalendar(
            false,
            takeMeToDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
            customCalendarEvents = emptyList()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 하위 컴포넌트들
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = {


                when (componentVariable.value) {
                    0 -> ScheduleCreateFirst()
                    1 -> ScheduleCreateSecond()
                    2 -> ScheduleCreateThird()
                    3 -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(0.88f)
                                .fillMaxHeight(),
                            content = {
                                val bottomSheetState =
                                    rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
                                val coroutineScope = rememberCoroutineScope()
                                ModalBottomSheetLayout(
                                    sheetContent = {
                                        LazyColumn (modifier = Modifier.fillMaxWidth()){
                                        when (memberType.value) {
                                            -1 -> item {
//                                                Box(modifier = Modifier
//                                                    .clickable {
//                                                        coroutineScope.launch {
//                                                            bottomSheetState.hide()
//                                                        }
//                                                    }
//                                                    .padding(vertical = 4.dp)
//                                                    .fillMaxWidth()
//                                                    .height(48.dp),
//                                                        contentAlignment = Alignment.Center) {
//                                                        Text(
//                                                            text = "Nuya 보관함에서 가져오기",
//                                                            color = PrimaryBlue,
//                                                            style = Typography.body1,
//                                                        )
//                                                    }
//                                                    Box(modifier = Modifier
//                                                        .clickable {
//                                                            coroutineScope.launch {
//                                                                bottomSheetState.hide()
//                                                            }
//                                                        }
//                                                        .padding(vertical = 4.dp)
//                                                        .fillMaxWidth()
//                                                        .height(48.dp),
//                                                        contentAlignment = Alignment.Center) {
//                                                        Text(
//                                                            text = "전화번호부에서 가져오기",
//                                                            color = PrimaryBlue,
//                                                            style = Typography.body1,
//                                                        )
//                                                    }
                                                    Box(modifier = Modifier
                                                        .clickable(
                                                            onClick = {
                                                                memberType.value = 0
                                                            })
                                                        .padding(vertical = 4.dp)
                                                        .fillMaxWidth()
                                                        .height(48.dp),
                                                        contentAlignment = Alignment.Center) {
                                                        Text(
                                                            text = "직접 입력",
                                                            color = PrimaryBlue,
                                                            style = Typography.body1,
                                                        )
                                                    }
                                            }

                                            0 -> item {
                                                Column(modifier = Modifier.fillMaxWidth(),
                                                    horizontalAlignment = Alignment.CenterHorizontally) {
                                                    MemberInput()
                                                    RegisterButton(
                                                        text = "등록",
                                                        onClick = {
                                                            viewModel.insertTemporaryMember(memberType.value,
                                                                memberNum.value % 6,
                                                                viewModel.schedulesAll.value.last().scheduleId?.plus(
                                                                    1) ?: 0)
                                                            memberNum.value += 1
                                                            memberType.value = -1

                                                            coroutineScope.launch {
                                                                bottomSheetState.hide()
                                                            } },
                                                    )
                                                    Spacer(modifier = Modifier.height(20.dp))
                                                }

                                                }
                                            }
                                            }
                                        },
                                    sheetState = bottomSheetState,
                                    scrimColor = Color(0XCCFFFFFF),
                                ) {
                                    Column {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth(0.88f)
                                                .height(320.dp)
                                        ) {
                                            Text("멤버 등록",
                                                modifier = Modifier.padding(vertical = 12.dp),
                                                color = PrimaryDark,
                                                fontFamily = fonts,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            )
                                            Text("멤버를 클릭하면 목록에서 삭제됩니다.", style = Typography.body2, color = SystemRed)
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Image(
                                                painter = painterResource(R.drawable.schedule_member_register_icon),
                                                contentDescription = "",
                                                modifier = Modifier
                                                    .width(64.dp)
                                                    .height(64.dp)
                                                    .clickable(
                                                        enabled = true,
                                                        onClick = {
                                                            coroutineScope.launch {
                                                                bottomSheetState.show()
                                                            }
                                                        }
                                                    )
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))

                                            LazyVerticalGrid(
                                                columns = GridCells.Fixed(5),
                                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            )
                                            {
                                                items(viewModel.memberList.value.size) { index ->
                                                    Row() {
                                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                            Image(
                                                                painter = painterResource(memberIcons[viewModel.memberList.value[index].memberIcon!!]),
                                                                contentDescription = "",
                                                                modifier = Modifier
                                                                    .width(60.dp)
                                                                    .height(60.dp)
                                                                    .clickable(
                                                                        enabled = true,
                                                                        onClick = {
                                                                            viewModel.deleteTemporaryMember(index)
                                                                        }
                                                                    )
                                                            )
                                                            viewModel.memberList.value[index].name?.let {
                                                                Text(
                                                                    it,
                                                                    color = NeutralGray,
                                                                    style = Typography.overline
                                                                )

                                                            }
                                                        }
                                                        Box(Modifier.width(16.dp).height(20.dp))
                                                    }
                                                }
                                            }

                                        }
                                        PrimaryBigButton(
                                            text = "다음",
                                            onClick = {
                                                componentVariable.value = componentVariable.value + 1
                                            },
                                        )
                                    }
                                }
                            }
                        )
                    }
                    4 -> ScheduleCreateFinal()
                }

                if (componentVariable.value != 3) {
                    PrimaryBigButton(
                        text = when (componentVariable.value) {
                            4 -> "등록하기"
                            else -> "다음"
                        },
                        onClick = {
                            componentVariable.value = componentVariable.value + 1
                            when (componentVariable.value) {
                                5 -> {
                                    viewModel.insertSchedule(selectedDate = viewModel.selectedDate.value)
                                    navController.navigate("schedule")
                                }
                            }
                        },
                    )
                }
        })
    }

}



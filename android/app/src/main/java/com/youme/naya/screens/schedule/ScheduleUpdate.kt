package com.youme.naya.screens.schedule

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.chargemap.compose.numberpicker.AMPMHours
import com.chargemap.compose.numberpicker.Hours
import com.chargemap.compose.numberpicker.HoursNumberPicker
import com.chargemap.compose.numberpicker.ListItemPicker
import com.youme.naya.R
import com.youme.naya.components.BasicTextField
import com.youme.naya.components.OutlinedSmallButton
import com.youme.naya.components.PrimaryBigButton
import com.youme.naya.components.PrimarySmallButton
import com.youme.naya.database.entity.Member
import com.youme.naya.database.entity.Schedule
import com.youme.naya.schedule.CustomAlertDialog
import com.youme.naya.schedule.ScheduleMainViewModel
import com.youme.naya.schedule.UiEvent
import com.youme.naya.ui.theme.*
import kotlinx.coroutines.launch

private val CalendarHeaderBtnGroupModifier = Modifier
    .fillMaxWidth()
    .height(64.dp)
    .padding(start = 8.dp, end = 8.dp)


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ScheduleUpdateScreen(
    navController: NavController,
    viewModel: ScheduleMainViewModel = hiltViewModel(),
    scheduleId: Int,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    // focus
    val focusRequester = remember { FocusRequester() }
    val openDialog = remember { mutableStateOf(false)  }



    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            CalendarHeaderBtnGroupModifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            TopAppBar(
                modifier = Modifier.height(64.dp),
                backgroundColor = NeutralWhite,
                elevation = 0.dp,
                contentPadding = PaddingValues(horizontal = 8.dp),
            ) {
                Row(Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Image(
                                painter = painterResource(R.drawable.ic_baseline_arrow_back_ios_24),
                                contentDescription = "Prev page button",
                                colorFilter = ColorFilter.tint(NeutralLight)
                            )
                        }
                        Text(
                            "일정 수정",
                            fontFamily = fonts,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = PrimaryDark,
                        )
                        Text(
                            "  삭제",
                            fontFamily = fonts,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = PrimaryBlue,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .clickable(onClick = {
                                    openDialog.value = true
                                })
                        )
                    }
                }
            }
        }
        // 삭제 시 모달
        DeleteModal(visible = openDialog.value,
            onDismissRequest = { openDialog.value = false },
            scheduleId = scheduleId,
            onDelete = {
                viewModel.deleteSchedule(scheduleId)
                navController.navigate("schedule")
            })

        Spacer(modifier = Modifier.height(20.dp))
        Column(modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                modifier = Modifier
                    .width(300.dp)
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,

                ) {
                Schedule.scheduleColors.forEach { color ->
                    val colorInt = color.toArgb()
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(color.copy(alpha = if (viewModel.color.value == colorInt) 1f else 0.3f))
                            .clickable(onClick = { viewModel.onColorChange(colorInt) }),
                    )

                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Column {
                Text(
                    "일정명",
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = PrimaryDark,
                    fontFamily = fonts,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )
                Spacer(modifier = Modifier.height(4.dp))
                BasicTextField(
                    modifier = Modifier
                        .focusRequester(focusRequester),
                    text = viewModel.title.value.text,
                    onChange = { viewModel.onTitleChange(it) },
                    placeholder = "일정명 입력",
                    imeAction = ImeAction.Done,
                    keyBoardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                        },
                    ),
                )
            }


            Spacer(modifier = Modifier.height(16.dp))
            // 시간 설정
            // 시작 시간 선택
            var pickerStartValue by remember {
                mutableStateOf<Hours>(AMPMHours(0,
                    0,
                    AMPMHours.DayTime.PM))
            }
            var pickerStartString = pickerStartValue.toString().reversed()

            fun StringConverter(start: String, end: String, AMPM: String): String {
                var start = start
                var end = end
                if (start.length == 1) {
                    start = "0$start"
                }
                if (end.length == 1) {
                    end = "0$end"
                }
                return "$start : $end $AMPM"
            }

            var showPickerStartDate by remember {
                mutableStateOf(false)
            }

            var showAlarmSetting by remember {
                mutableStateOf(false)
            }


            // 끝나는 시간
            var pickerEndValue by remember {
                mutableStateOf<Hours>(AMPMHours(12,
                    0,
                    AMPMHours.DayTime.PM))
            }
            var pickerEndString = pickerEndValue.toString().reversed()

            var showPickerEndDate by remember {
                mutableStateOf(false)
            }

            Column(modifier = Modifier.width(300.dp)) {
                Text("시간 설정",
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = PrimaryDark,
                    fontFamily = fonts,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Row(modifier = Modifier
                    .width(300.dp)
                    .clickable(onClick = { showPickerStartDate = !showPickerStartDate }),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("시작 시간",
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = NeutralGray,
                        fontFamily = fonts,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp
                    )
                    Text(
                        viewModel.startTime.value,
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = PrimaryDark,
                        fontFamily = fonts,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp,
                    )

                }
            }
            if (showPickerStartDate) {
                Column(modifier = Modifier
                    .width(300.dp)
                    .padding(horizontal = 8.dp, vertical = 10.dp)) {
                    HoursNumberPicker(
                        dividersColor = SecondaryLightBlue,
                        value = pickerStartValue,
                        onValueChange = {
                            pickerStartValue = it
                            pickerStartString = it.toString().reversed()
                            viewModel.onStartTimeChange(StringConverter(it.hours.toString(),
                                it.minutes.toString(),
                                pickerStartString.substring(1, 3).reversed()))
                        },
                        hoursDivider = {
                            Text(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp),
                                textAlign = TextAlign.Center,
                                text = ":"
                            )
                        },
                        minutesDivider = {
                            Text(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                textAlign = TextAlign.Center,
                                text = " "
                            )
                        })

                }
            }
            Row(modifier = Modifier
                .width(300.dp)
                .clickable(onClick = { showPickerEndDate = !showPickerEndDate }),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text("종료 시간",
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = NeutralGray,
                    fontFamily = fonts,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp
                )
                Text(
                    viewModel.endTime.value,
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = PrimaryDark,
                    fontFamily = fonts,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                )

            }
            if (showPickerEndDate) {
                Column(modifier = Modifier
                    .width(300.dp)
                    .padding(horizontal = 8.dp, vertical = 10.dp)) {
                    HoursNumberPicker(
                        dividersColor = SecondaryLightBlue,
                        value = pickerEndValue,
                        onValueChange = {
                            pickerEndValue = it
                            pickerEndString = it.toString().reversed()
                            viewModel.onEndTimeChange(StringConverter(it.hours.toString(),
                                it.minutes.toString(),
                                pickerEndString.substring(1, 3).reversed()))
                        },
                        hoursDivider = {
                            Text(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp),
                                textAlign = TextAlign.Center,
                                text = ":"
                            )
                        },
                        minutesDivider = {
                            Text(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                textAlign = TextAlign.Center,
                                text = " "
                            )
                        })

                }
            }
            Divider(modifier = Modifier
                .width(300.dp)
                .padding(vertical = 20.dp), color = PrimaryLight)
            Row(modifier = Modifier
                .width(300.dp)
                .clickable(onClick = { showPickerEndDate = !showPickerEndDate }),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text("일정 알람",
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = NeutralGray,
                    fontFamily = fonts,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp
                )
                Switch(
                    checked = viewModel.isOnAlarm.value,
                    onCheckedChange = { viewModel.onAlarmChange() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = NeutralWhite,
                        checkedTrackColor = PrimaryBlue,
                        uncheckedThumbColor = NeutralWhite,
                        uncheckedTrackColor = NeutralLightness
                    )
                )

            }

            if (viewModel.isOnAlarm.value) {
                val possibleValues = listOf("시작 시간", "1시간 전", "3시간 전", "종료 시간")
                var state by remember { mutableStateOf(possibleValues[0]) }
                Row(modifier = Modifier
                    .width(300.dp)
                    .clickable(onClick = { showAlarmSetting = !showAlarmSetting }),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("알람 시간 설정",
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = NeutralGray,
                        fontFamily = fonts,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp
                    )
                    Text(
                        viewModel.alarmTime.value,
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = PrimaryDark,
                        fontFamily = fonts,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp,
                    )

                }
                if (showAlarmSetting) {
                    Column(modifier = Modifier
                        .width(300.dp)
                        .padding(vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ListItemPicker(
                            dividersColor = SecondaryLightBlue,
                            label = { it },
                            value = viewModel.alarmTime.value,
                            onValueChange = { viewModel.alarmTimeChange(it) },
                            list = possibleValues
                        )

                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            // 주소 등록
            Column {
                Text(
                    "주소 등록",
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = PrimaryDark,
                    fontFamily = fonts,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                BasicTextField(
                    modifier = Modifier
                        .focusRequester(focusRequester),
                    text = viewModel.address.value.text,
                    onChange = { viewModel.onAddressChange(it) },
                    placeholder = "주소 입력",
                    imeAction = ImeAction.Done,
                    keyBoardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                    }),
                )}
                Spacer(modifier = Modifier.height(16.dp))
            Column {
                Text(
                "함께 하는 멤버",
                modifier = Modifier.padding(vertical = 12.dp),
                color = PrimaryDark,
                fontFamily = fonts,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("멤버를 클릭하면 목록에서 삭제됩니다.", style = Typography.body2, color = SystemRed)
                Spacer(modifier = Modifier.height(12.dp))
                LazyVerticalGrid(
                    modifier = Modifier.height(80.dp).width(300.dp),
                    columns = GridCells.Fixed(5),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                )
                {
                    items(viewModel.memberList.value.size) { index ->
                        Row() {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Image(
                                    painter = painterResource(Member.memberIcons[viewModel.memberList.value[index].memberIcon!!]),
                                    contentDescription = "",
                                    modifier = Modifier
                                        .width(60.dp)
                                        .height(60.dp)
                                        .clickable(
                                            enabled = true,
                                            onClick = {
                                                viewModel.deleteMember(viewModel.memberList.value[index])
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
                }}


                Spacer(modifier = Modifier.height(16.dp))
            Column() {
                // 추가 기록 사항
                Text(
                    "추가 기록 사항",
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = PrimaryDark,
                    fontFamily = fonts,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                BasicTextField(
                    modifier = Modifier
                        .focusRequester(focusRequester),
                    text = viewModel.description.value.text,
                    onChange = { viewModel.onDescriptionChange(it) },
                    placeholder = "추가 기록 사항 입력",
                    imeAction = ImeAction.Done,
                    keyBoardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                    })
                )}
                Spacer(modifier = Modifier.height(80.dp))
                PrimaryBigButton(text = "수정하기",
                    onClick = {
                        viewModel.insertSchedule(
                            selectedDate = viewModel.selectedDate.value
                        )
                        navController.navigate("schedule")
                    })
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
}

@Composable
fun DeleteModal(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    scheduleId: Int,
    onDelete: () -> Unit,
) {
    if (visible) {
        CustomAlertDialog(onDismissRequest = { onDismissRequest() }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.55f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color = NeutralWhite),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .padding(horizontal = 24.dp)
                    ,
                    text = "삭제 시 복구가 불가능합니다 \n 정말 삭제하시겠습니까?" ,
                    style = Typography.h6,
                    color = PrimaryDark,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(32.dp))
                Row() {
                    OutlinedSmallButton(text = "취소", onClick = {
                        onDismissRequest()})
                    Spacer(modifier = Modifier.width(14.dp))
                    PrimarySmallButton(text = "삭제", onClick = {
                        onDelete()
                        onDismissRequest()})
                }
            }
        }
    }
}


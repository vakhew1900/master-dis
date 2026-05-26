import React from "react";
import {
  Typography,
  Grid,
  Card,
  CardContent,
  CardActions,
  Button,
  Box,
} from "@mui/material";
import { useNavigate } from "react-router-dom";
import ScienceIcon from "@mui/icons-material/Science";
import PeopleIcon from "@mui/icons-material/People";
import MoreHorizIcon from "@mui/icons-material/MoreHoriz";

const AdminPage: React.FC = () => {
  const navigate = useNavigate();

  const sections = [
    {
      title: "Лабораторные работы",
      description:
        "Управление лабораторными работами и заданиями для студентов.",
      icon: <ScienceIcon color="primary" sx={{ fontSize: 40 }} />,
      action: "Перейти к работам",
      path: "/admin/labs",
    },
    {
      title: "Студенты",
      description:
        "Просмотр списка студентов, их прогресса и управление доступами.",
      icon: <PeopleIcon color="primary" sx={{ fontSize: 40 }} />,
      action: "Список студентов",
      path: "/admin/students", // Stub for now
    },
    {
      title: "Прочее",
      description: "Дополнительные настройки системы и отчеты.",
      icon: <MoreHorizIcon color="primary" sx={{ fontSize: 40 }} />,
      action: "Открыть",
      path: "#",
    },
  ];

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" gutterBottom component="h1" sx={{ mb: 4 }}>
        Панель администратора
      </Typography>

      <Grid container spacing={3}>
        {sections.map((section) => (
          <Grid key={section.title} size={{ xs: 12, md: 4 }}>
            <Card
              sx={{ height: "100%", display: "flex", flexDirection: "column" }}
            >
              <CardContent sx={{ flexGrow: 1 }}>
                <Box sx={{ display: "flex", alignItems: "center", mb: 2 }}>
                  {section.icon}
                  <Typography variant="h5" component="h2" sx={{ ml: 2 }}>
                    {section.title}
                  </Typography>
                </Box>
                <Typography variant="body2" color="text.secondary">
                  {section.description}
                </Typography>
              </CardContent>
              <CardActions sx={{ p: 2, pt: 0 }}>
                <Button
                  variant="contained"
                  fullWidth
                  onClick={() => section.path !== "#" && navigate(section.path)}
                >
                  {section.action}
                </Button>
              </CardActions>
            </Card>
          </Grid>
        ))}
      </Grid>
    </Box>
  );
};

export default AdminPage;

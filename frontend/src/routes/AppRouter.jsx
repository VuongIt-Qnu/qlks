import { Routes, Route } from "react-router-dom";
import Layout from "../components/Layout";
import ProtectedRoute from "../components/ProtectedRoute";
import RoleRoute from "../components/RoleRoute";
import HomePage from "../pages/HomePage";
import LoginPage from "../pages/LoginPage";
import RegisterPage from "../pages/RegisterPage";
import DashboardPage from "../pages/DashboardPage";
import UserBookingsPage from "../pages/UserBookingsPage";
import AdminUsersPage from "../pages/AdminUsersPage";
import HotelsPage from "../pages/HotelsPage";
import RoomsPage from "../pages/RoomsPage";
import NotFoundPage from "../pages/NotFoundPage";
import SearchPage from "../pages/SearchPage";
import NewBookingPage from "../pages/NewBookingPage";
import PaymentPage from "../pages/PaymentPage";
import OwnerBookingsPage from "../pages/OwnerBookingsPage";

function AppRouter() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route path="/" element={<HomePage />} />
        <Route path="/search" element={<SearchPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              <DashboardPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/booking/new"
          element={
            <ProtectedRoute>
              <RoleRoute roles={["USER"]}>
                <NewBookingPage />
              </RoleRoute>
            </ProtectedRoute>
          }
        />
        <Route
          path="/payments/:id"
          element={
            <ProtectedRoute>
              <RoleRoute roles={["USER"]}>
                <PaymentPage />
              </RoleRoute>
            </ProtectedRoute>
          }
        />
        <Route
          path="/bookings"
          element={
            <ProtectedRoute>
              <RoleRoute roles={["USER"]}>
                <UserBookingsPage />
              </RoleRoute>
            </ProtectedRoute>
          }
        />
        <Route
          path="/admin/users"
          element={
            <ProtectedRoute>
              <RoleRoute roles={["ADMIN"]}>
                <AdminUsersPage />
              </RoleRoute>
            </ProtectedRoute>
          }
        />
        <Route
          path="/hotels"
          element={
            <ProtectedRoute>
              <RoleRoute roles={["ADMIN", "OWNER"]}>
                <HotelsPage />
              </RoleRoute>
            </ProtectedRoute>
          }
        />
        <Route
          path="/rooms"
          element={
            <ProtectedRoute>
              <RoleRoute roles={["ADMIN", "OWNER"]}>
                <RoomsPage />
              </RoleRoute>
            </ProtectedRoute>
          }
        />
        <Route
          path="/owner/bookings"
          element={
            <ProtectedRoute>
              <RoleRoute roles={["OWNER"]}>
                <OwnerBookingsPage />
              </RoleRoute>
            </ProtectedRoute>
          }
        />
        <Route path="*" element={<NotFoundPage />} />
      </Route>
    </Routes>
  );
}

export default AppRouter;

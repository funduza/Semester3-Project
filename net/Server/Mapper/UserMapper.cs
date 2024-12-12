using DTOs;
using Server.Grpc;

namespace Server.Mapper;

public static class UserMapper
{
    public static UserDto ToDto(UserProto proto)
    {
        return proto.Role switch
        {
            "JobSeeker" => new UserDto()
            {
                Id = proto.Id,
                Email = proto.Email,
                Password = proto.Password,
                Role = proto.Role,
                FirstName = proto.JobSeeker.FirstName,
                LastName = proto.JobSeeker.LastName,
                PhoneNumber = proto.JobSeeker.PhoneNumber,
                Resume = proto.JobSeeker.Resume
            },
            "JobProvider" => new UserDto()
            {
                Id = proto.Id,
                Email = proto.Email,
                Password = proto.Password,
                Role = proto.Role,
                Name = proto.JobProvider.Name,
                Description = proto.JobProvider.Description,
                PhoneNumber = proto.JobProvider.PhoneNumber,
            },
            _ => throw new ArgumentException("Role not supported, use one of: JobSeeker, JobProvider.")
        };
    }

    public static UserProto ToProto(UserDto dto)
    {
        return dto.Role switch
        {
            "JobSeeker" => new UserProto()
            {
                Id = dto.Id,
                Email = dto.Email,
                Password = dto.Password,
                Role = dto.Role,
                JobSeeker = new JobSeekerProto()
                {
                    FirstName = dto.FirstName,
                    LastName = dto.LastName,
                    PhoneNumber = dto.PhoneNumber,
                    Resume = dto.Resume   
                }
            },
            "JobProvider" => new UserProto()
            {
                Id = dto.Id,
                Email = dto.Email,
                Password = dto.Password,
                Role = dto.Role,
                JobProvider = new JobProviderProto() {
                    Name = dto.Name,
                    Description = dto.Description,
                    PhoneNumber = dto.PhoneNumber,
                }
            },
            _ => throw new ArgumentException("Role not supported, use one of: JobSeeker, JobProvider.")
        };
    }
}